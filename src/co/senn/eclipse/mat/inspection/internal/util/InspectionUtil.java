package co.senn.eclipse.mat.inspection.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import co.senn.eclipse.mat.inspection.api.IInspection;
import co.senn.eclipse.mat.inspection.api.ITechnology;
import co.senn.eclipse.mat.inspection.internal.spec.InspectionSpec;
import co.senn.eclipse.mat.inspection.internal.spec.TechnologySpec;

public final class InspectionUtil {

	private InspectionUtil() {
	}

	public static Map<TechnologySpec, List<InspectionSpec>> getInspections() throws Exception {
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

}
