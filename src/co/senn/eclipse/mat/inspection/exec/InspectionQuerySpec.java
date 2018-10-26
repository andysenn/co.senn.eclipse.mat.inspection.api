package co.senn.eclipse.mat.inspection.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.IResult;
import org.eclipse.mat.query.annotations.Argument;
import org.eclipse.mat.query.annotations.CommandName;
import org.eclipse.mat.query.results.TextResult;
import org.eclipse.mat.report.QuerySpec;
import org.eclipse.mat.report.SectionSpec;
import org.eclipse.mat.report.Spec;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.util.IProgressListener;

import co.senn.eclipse.mat.inspection.api.IInspection;
import co.senn.eclipse.mat.inspection.api.IInspectionResult;
import co.senn.eclipse.mat.inspection.api.ITechnology;
import co.senn.eclipse.mat.inspection.api.Ignore;
import co.senn.eclipse.mat.inspection.api.InspectionResultSeverity;

@CommandName("inspections:suspects")
public class InspectionQuerySpec implements IQuery {

	@Argument
	public ISnapshot snapshot;

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
		listener.subTask("Resolving inspections");

		Map<TechnologySpec, List<InspectionSpec>> inspectionsByTechnology = getInspections();

		listener.beginTask("Inspecting technologies", inspectionsByTechnology.size());

		Collection<TechnologySpec> presentTechnologies = new ArrayList<>();
		Collection<TechnologySpec> ignoredTechnologies = new ArrayList<>();
		Collection<TechnologySpec> missingTechnologies = new ArrayList<>();

		SectionSpec parent = new SectionSpec("Inspection Report");

		TechnologySpec technology;
		List<InspectionSpec> inspections;
		for (Entry<TechnologySpec, List<InspectionSpec>> entry : inspectionsByTechnology.entrySet()) {
			technology = entry.getKey();
			inspections = entry.getValue();

			if (isIgnored(technology.getClass())) {
				ignoredTechnologies.add(technology);
			} else {
				listener.subTask("Inspecting " + technology.getName());

				try {
					if (technology.getTechnology().isPresent(snapshot)) {
						presentTechnologies.add(technology);
						parent.add(createTechnologySection(technology, inspections, listener));
					} else {
						missingTechnologies.add(technology);
					}
				} finally {
					listener.worked(1);
				}
			}
		}

		if (missingTechnologies.size() > 0) {
			StringBuilder sb = new StringBuilder("The following technologies are supported, but were not detected:\n");
			for (TechnologySpec missingTechnology : missingTechnologies) {
				sb.append("\n").append(missingTechnology.getName());
			}

			QuerySpec missingTechnologySection = new QuerySpec("Undetected Technologies",
					new TextResult(sb.toString()));
			missingTechnologySection.set("html.collapsed", "true");

			parent.add(missingTechnologySection);
		}

		return parent;
	}

	private Map<TechnologySpec, List<InspectionSpec>> getInspections() throws Exception {
		IConfigurationElement[] technologyConfigs = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("co.senn.eclipse.mat.inspection.technology");
		IConfigurationElement[] inspectionConfigs = Platform.getExtensionRegistry()
				.getConfigurationElementsFor("co.senn.eclipse.mat.inspection.inspection");

		Collection<TechnologySpec> technologySpecs = new ArrayList<>();
		for (IConfigurationElement config : technologyConfigs) {
			Object executable = config.createExecutableExtension("impl");
			if (executable instanceof ITechnology) {
				// @formatter:off
				technologySpecs.add(new TechnologySpec(
						config.getAttribute("id"),
						config.getAttribute("name"),
						config.getAttribute("description"),
						(ITechnology) executable
				));
				// @formatter:on
			}
		}

		Map<String, TechnologySpec> technologiesById = technologySpecs.stream()
				.collect(Collectors.toMap(TechnologySpec::getId, Function.identity()));

		Collection<InspectionSpec> inspectionSpecs = new ArrayList<>();
		for (IConfigurationElement config : inspectionConfigs) {
			Object executable = config.createExecutableExtension("impl");
			if (executable instanceof IInspection) {
				// @formatter:off
				inspectionSpecs.add(new InspectionSpec(
						config.getAttribute("id"),
						config.getAttribute("name"),
						config.getAttribute("description"),
						technologiesById.get(config.getAttribute("technology")),
						(IInspection) executable
				));
				// @formatter:on
			}
		}

		return inspectionSpecs.stream().collect(Collectors.groupingBy(InspectionSpec::getTechnology));
	}

	private boolean isIgnored(Class<?> clazz) {
		return clazz.isAnnotationPresent(Ignore.class);
	}

	private Spec createTechnologySection(TechnologySpec technology, List<InspectionSpec> inspections,
			IProgressListener listener) throws Exception {
		SectionSpec technologySection = new SectionSpec(technology.getName());
		Map<InspectionResultSeverity, AtomicInteger> severityCounts = new HashMap<>();
		IInspectionResult result;
		for (InspectionSpec inspection : inspections) {
			if (!isIgnored(inspection.getClass())) {
				result = inspection.getInspection().execute(snapshot, listener);
				if (result != null) {
					severityCounts.computeIfAbsent(result.getSeverity(), s -> new AtomicInteger(0)).incrementAndGet();
					technologySection.add(new QuerySpec(inspection.getName() + " - " + result.getSeverity().getName(),
							result.getResult()));
				}
			}
		}

		if (severityCounts.size() > 0) {
			StringBuilder sb = new StringBuilder(technologySection.getName()).append(" (");
			sb.append(Stream.of(InspectionResultSeverity.values()).filter(severityCounts::containsKey)
					.map(s -> severityCounts.get(s) + " " + s.getName()).collect(Collectors.joining(", ")));
			sb.append(")");

			technologySection.setName(sb.toString());
		}

		return technologySection;
	}

	private static abstract class AbstractSpec {

		private final String id;
		private final String name;
		private final String description;

		public AbstractSpec(String id, String name, String description) {
			this.id = id;
			this.name = name;
			this.description = description;
		}

		public String getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public String getDescription() {
			return description;
		}

	}

	private static final class TechnologySpec extends AbstractSpec {

		private final ITechnology technology;

		public TechnologySpec(String id, String name, String description, ITechnology technology) {
			super(id, name, description);
			this.technology = technology;
		}

		public ITechnology getTechnology() {
			return technology;
		}

	}

	private static final class InspectionSpec extends AbstractSpec {

		private final TechnologySpec technology;
		private final IInspection inspection;

		public InspectionSpec(String id, String name, String description, TechnologySpec technology,
				IInspection inspection) {
			super(id, name, description);
			this.technology = technology;
			this.inspection = inspection;
		}

		public TechnologySpec getTechnology() {
			return technology;
		}

		public IInspection getInspection() {
			return inspection;
		}

	}

}
