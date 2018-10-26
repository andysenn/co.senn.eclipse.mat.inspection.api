package co.senn.eclipse.mat.inspection.api;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;

/**
 * An interface for the various technologies (libraries, containers, etc) that
 * may be found in a heap dump.
 * 
 * @author Andy Senn
 */
public interface ITechnology {

	boolean isPresent(ISnapshot snapshot) throws SnapshotException;

}
