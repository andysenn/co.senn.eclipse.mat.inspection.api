package co.senn.eclipse.mat.inspection.api;

public enum InspectionResultSeverity {
	INFO("Info"), WARN("Warning"), ERROR("Error");

	private final String name;

	InspectionResultSeverity(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
