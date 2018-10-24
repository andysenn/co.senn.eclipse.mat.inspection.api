package co.senn.eclipse.mat.ext;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.query.IQuery;
import org.eclipse.mat.query.annotations.Argument;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.IProgressListener;

public abstract class AbstractQuery implements IQuery {

	@Argument
	public ISnapshot snapshot;

	public abstract String getSubjectName();

	public Object executeQuery(String query, IProgressListener listener) throws SnapshotException {
		return SnapshotFactory.createQuery(query).execute(snapshot, listener);
	}

	/**
	 * Attempts to parse the provided Object as an Integer. If successful, the
	 * Integer value is passed to the specified consumer. This method also returns a
	 * boolean indicating whether the provided Object could be parsed as an Integer.
	 * 
	 * @param object The object to be parsed to an Integer
	 * @param ifInt  The consumer to which the Integer value will be passed, if
	 *               properly parsed
	 * @return A boolean indicating whether the provided Object could be parsed as
	 *         an Integer
	 */
	public boolean tryParseInt(Object object, Consumer<Integer> ifInt) {
		try {
			ifInt.accept(Integer.valueOf(String.valueOf(object)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Queries the snapshot for any classes by the specified class name, including
	 * sub-types, if specified, and invokes the provided consumer for each instance
	 * thereof. This method also returns a boolean indicating whether any classes by
	 * the specified criteria were found.
	 * 
	 * @param type     The fully-qualified name of the class
	 * @param subtypes If true, includes sub-types of the specified class
	 * @param consumer The consumer by which all found object instances will be
	 *                 processed
	 * @return A boolean indicating whether any classes by the specified criteria
	 *         were found
	 * @throws SnapshotException
	 * @see {@link #getObjects(String, boolean)}
	 */
	public boolean forEachObjectOfType(String type, boolean subtypes, SnapshotConsumer<IObject> consumer)
			throws SnapshotException {
		Collection<IObject> objects = getObjects(type, subtypes);

		for (IObject object : objects) {
			consumer.accept(object);
		}

		return objects.size() > 0;
	}

	/**
	 * Queries the snapshot for any classes by the specified class name, including
	 * sub-types, if specified, and returns all instances thereof.
	 * 
	 * @param type     The fully-qualified name of the class
	 * @param subtypes If true, includes sub-types of the specified class
	 * @return A collection of object instances matching the specified class name
	 * @throws SnapshotException
	 * @see #forEachObjectOfType(String, boolean, SnapshotConsumer)
	 */
	public Collection<IObject> getObjects(String type, boolean subtypes) throws SnapshotException {
		Collection<IClass> classes = snapshot.getClassesByName(type, subtypes);

		Collection<IObject> objects = new HashSet<>();
		if (classes != null) {
			for (IClass clazz : classes) {
				for (int objectId : clazz.getObjectIds()) {
					objects.add(snapshot.getObject(objectId));
				}
			}
		}

		return objects;
	}

	/**
	 * Returns the message to be displayed if the technology being interrogated does
	 * not appear in the heap dump.
	 * 
	 * @return The message to be displayed if the technology being interrogated does
	 *         not appear in the heap dump.
	 */
	public String messageNotDetected() {
		return getSubjectName() + " Not Detected";
	}

	/**
	 * Returns the message to be displayed if the technology being interrogated
	 * appears in the heap dump, but no issues were found.
	 * 
	 * @return The message to be displayed if the technology being interrogated
	 *         appears in the heap dump, but no issues were found.
	 */
	public String messageNoIssues() {
		return "No issues found";
	}

	/**
	 * A simple, functional interface that accepts a value and returns nothing, with
	 * the option of throwing a {@link SnapshotException}.
	 * 
	 * @param <T> The type of the object(s) to be consumed
	 * @author Andy Senn
	 */
	@FunctionalInterface
	public static interface SnapshotConsumer<T> {
		void accept(T t) throws SnapshotException;
	}

}
