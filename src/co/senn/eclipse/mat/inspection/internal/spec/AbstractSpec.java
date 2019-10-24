package co.senn.eclipse.mat.inspection.internal.spec;

public abstract class AbstractSpec {

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