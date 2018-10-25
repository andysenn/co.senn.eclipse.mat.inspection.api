package co.senn.eclipse.mat.ext.inspection;

import org.eclipse.mat.SnapshotException;

/**
 * An interface for the various technologies (libraries, containers, etc) that
 * may be found in a heap dump.
 * 
 * @author Andy Senn
 */
public interface Technology {

	String getName();

	boolean isPresent() throws SnapshotException;

	Inspection[] getInspections();

}
