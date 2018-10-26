package co.senn.eclipse.mat.inspection.api;

import java.util.Collection;
import java.util.regex.Pattern;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.model.IClass;

/**
 * An interface for the various technologies (libraries, containers, etc) that
 * may be found in a heap dump.
 * 
 * @author Andy Senn
 */
public interface ITechnology {

	boolean isPresent(ISnapshot snapshot) throws SnapshotException;

	default boolean isPackagePresent(ISnapshot snapshot, String packageName) throws SnapshotException {
		Collection<IClass> classes = snapshot.getClassesByName(Pattern.compile(packageName + ".*"), true);
		return classes != null && classes.size() > 0;
	}

}
