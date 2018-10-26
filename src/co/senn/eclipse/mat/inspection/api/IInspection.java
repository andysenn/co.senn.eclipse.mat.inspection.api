package co.senn.eclipse.mat.inspection.api;

import org.eclipse.mat.query.IResult;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.util.IProgressListener;

/**
 * An interface for inspections that can be performed for a given
 * {@link ITechnology}.
 * <p>
 * An inspection tests for a specific condition and reports any pertinent issues
 * that were found.
 * 
 * @author Andy Senn
 */
public interface IInspection {

	IResult execute(ISnapshot snapshot, IProgressListener listener) throws Exception;

}
