package co.senn.eclipse.mat.inspection.internal.spec;

import co.senn.eclipse.mat.inspection.api.IInspection;

public final class InspectionSpec extends AbstractSpec {

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