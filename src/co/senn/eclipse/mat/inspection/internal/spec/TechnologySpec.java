package co.senn.eclipse.mat.inspection.internal.spec;

import co.senn.eclipse.mat.inspection.api.ITechnology;

public final class TechnologySpec extends AbstractSpec {

	private final ITechnology technology;

	public TechnologySpec(String id, String name, String description, ITechnology technology) {
		super(id, name, description);
		this.technology = technology;
	}

	public ITechnology getTechnology() {
		return technology;
	}

}