package co.senn.eclipse.mat.inspection.api;

import org.eclipse.mat.query.IResult;

public interface IInspectionResult {

	IResult getResult();

	InspectionResultSeverity getSeverity();

}
