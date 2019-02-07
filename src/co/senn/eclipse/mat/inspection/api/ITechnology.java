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

	/**
	 * Returns a boolean indicating whether this technology is present in the
	 * specified snapshot.
	 * 
	 * @param snapshot The snapshot to be queried
	 * @return A boolean indicating whether this technology is present
	 * @throws SnapshotException
	 */
	boolean isPresent(ISnapshot snapshot) throws SnapshotException;

	/**
	 * Queries the specified snapshot for classes in the package by the specified
	 * name and returns a boolean indicating whether any were found.
	 * 
	 * @param snapshot    The snapshot to be queried
	 * @param packageName The name of the package to be queried
	 * @return A boolean indicating whether any classes for the specified package
	 *         were found
	 * @throws SnapshotException
	 */
	static boolean isPackagePresent(ISnapshot snapshot, String packageName) throws SnapshotException {
		Collection<IClass> classes = snapshot.getClassesByName(Pattern.compile(packageName + ".*"), true);
		return classes != null && classes.size() > 0;
	}

}
