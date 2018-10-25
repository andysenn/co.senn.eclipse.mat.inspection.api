package co.senn.eclipse.mat.ext.inspection;

import org.eclipse.mat.query.IResult;
import org.eclipse.mat.util.IProgressListener;

/**
 * An interface for inspections that can be performed for a given
 * {@link Technology}.
 * <p>
 * An inspection tests for a specific condition and reports any pertinent issues
 * that were found.
 * 
 * @author Andy Senn
 */
public interface Inspection {

	IResult execute(IProgressListener listener) throws Exception;

}
