/*
 * Copyright 2018 Andy Senn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package co.senn.eclipse.mat.inspection.util;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.ISnapshot;
import org.eclipse.mat.snapshot.SnapshotFactory;
import org.eclipse.mat.snapshot.model.IClass;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.util.IProgressListener;

/**
 * A collection of helper methods for querying snapshots and processing results.
 * 
 * @author Andy Senn
 */
public final class InspectionUtil {

	private InspectionUtil() {
	}

	/**
	 * Creates and executes the specified query against the specified snapshot and
	 * returns the result as an Object.
	 * 
	 * @param query    The query to be executed
	 * @param snapshot The snapshot to be queried
	 * @param listener The progress listener
	 * @return The result of the query as an Object
	 * @throws SnapshotException
	 */
	public static Object executeQuery(String query, ISnapshot snapshot, IProgressListener listener)
			throws SnapshotException {
		return SnapshotFactory.createQuery(query).execute(snapshot, listener);
	}

	/**
	 * Queries the snapshot for any classes by the specified class name, including
	 * sub-types, if specified, and invokes the provided consumer for each instance
	 * thereof. This method also returns a boolean indicating whether any classes by
	 * the specified criteria were found.
	 * 
	 * @param type     The fully-qualified name of the class
	 * @param subtypes If true, includes sub-types of the specified class
	 * @param snapshot The snapshot to be queried
	 * @param consumer The consumer by which all found object instances will be
	 *                 processed
	 * @return A boolean indicating whether any classes by the specified criteria
	 *         were found
	 * @throws SnapshotException
	 * @see #getObjects(String, boolean)
	 */
	public static boolean forEachObjectOfType(String type, boolean subtypes, ISnapshot snapshot,
			SnapshotConsumer<IObject> consumer) throws SnapshotException {
		Collection<IObject> objects = getObjects(type, subtypes, snapshot);

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
	 * @param snapshot The snapshot to be queried
	 * @return A collection of object instances matching the specified class name
	 * @throws SnapshotException
	 * @see #forEachObjectOfType(String, boolean, SnapshotConsumer)
	 */
	public static Collection<IObject> getObjects(String type, boolean subtypes, ISnapshot snapshot)
			throws SnapshotException {
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
