/*
 * Copyright 2018 Andy Senn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.senn.eclipse.mat.inspection.exec;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
public final class InspectionQuerySpec implements IQuery {

	@Argument
	public ISnapshot snapshot;

	@Override
	public IResult execute(IProgressListener listener) throws Exception {
		listener.subTask("Resolving inspections");

		// Lookup all of the technologies and their inspections
		Map<TechnologySpec, List<InspectionSpec>> inspectionsByTechnology = getInspections();

		listener.beginTask("Inspecting technologies", inspectionsByTechnology.size());

		// Technologies that were present in the snapshot and for which inspections were executed
		Collection<TechnologySpec> presentTechnologies = new ArrayList<>();

		// Technologies that were annotated with {@link Ignore}, whether present or not
		Collection<TechnologySpec> ignoredTechnologies = new ArrayList<>();

		// Technologies that were not present in the snapshot
		Collection<TechnologySpec> missingTechnologies = new ArrayList<>();

		// Technologies that were skipped due to cancellation, whether present or not
		Collection<TechnologySpec> skippedTechnologies = new ArrayList<>();

		// Create the parent section
		SectionSpec parent = new SectionSpec("Inspection Report");

		Iterator<Entry<TechnologySpec, List<InspectionSpec>>> iterator = inspectionsByTechnology.entrySet().iterator();
		Entry<TechnologySpec, List<InspectionSpec>> entry;
		TechnologySpec technology;
		List<InspectionSpec> inspections;
		while (iterator.hasNext()) {
			entry = iterator.next();
			technology = entry.getKey();
			inspections = entry.getValue();

			if (isIgnored(technology.getClass())) {
				ignoredTechnologies.add(technology);
			} else {
				listener.subTask("Inspecting " + technology.getName());

				try {
					if (technology.getTechnology().isPresent(snapshot)) {
						presentTechnologies.add(technology);
						Spec technologySection = createTechnologySection(technology, inspections, listener);
						if (technologySection != null) {
							parent.add(technologySection);
						}
					} else {
						missingTechnologies.add(technology);
					}
				} finally {
					listener.worked(1);
				}
			}

			if (listener.isCanceled()) {
				break;
			}
		}

		// If there were no results, then report that no issues were found
		if (parent.getChildren().isEmpty()) {
			parent.add(new SectionSpec("No issues found"));
		}

		// If there are still technologies on the iterator, they were skipped
		while (iterator.hasNext()) {
			entry = iterator.next();
			skippedTechnologies.add(entry.getKey());
		}

		// If there were skipped technologies, print a list of them in a collapsed section
		if (skippedTechnologies.size() > 0) {
			parent.add(createTechnologyListSpec("The following technologies were skipped:", "Skipped Technologies",
					skippedTechnologies));
		}

		// If there were non-present technologies, print a list of them in a collapsed section
		if (missingTechnologies.size() > 0) {
			parent.add(createTechnologyListSpec("The following technologies are supported, but were not detected:",
					"Undetected Technologies", missingTechnologies));
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

		Map<TechnologySpec, List<InspectionSpec>> inspectionsByTechnology = inspectionSpecs.stream()
				.filter(i -> i.getTechnology() != null).collect(Collectors.groupingBy(InspectionSpec::getTechnology));

		List<InspectionSpec> otherInspections = inspectionSpecs.stream().filter(i -> i.getTechnology() == null)
				.collect(Collectors.toList());

		if (otherInspections.size() > 0) {
			inspectionsByTechnology.put(new TechnologySpec("", "Other", "", s -> true), otherInspections);
		}

		return inspectionsByTechnology;
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
			technologySection.setName(String.format("%s (%s)", technologySection.getName(),
					Stream.of(InspectionResultSeverity.values()).filter(severityCounts::containsKey)
							.map(s -> severityCounts.get(s) + " " + s.getName()).collect(Collectors.joining(", "))));

			return technologySection;
		} else {
			return null;
		}
	}

	private Spec createTechnologyListSpec(String message, String header, Collection<TechnologySpec> technologySpecs) {
		StringBuilder sb = new StringBuilder(message).append("\n");
		for (TechnologySpec technologySpec : technologySpecs) {
			sb.append("\n").append(technologySpec.getName());
		}

		QuerySpec technologyListSpec = new QuerySpec(header, new TextResult(sb.toString()));
		technologyListSpec.set("html.collapsed", "true");

		return technologyListSpec;
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
