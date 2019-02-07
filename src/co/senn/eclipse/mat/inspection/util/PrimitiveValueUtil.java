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

import java.util.function.Consumer;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;

/**
 * A collection of helper methods for extracting primitive values from
 * {@linkplain IObject heap objects}.
 * 
 * @author Andy Senn
 */
public final class PrimitiveValueUtil {

	private PrimitiveValueUtil() {
	}

	/**
	 * Parses and returns the string representation of the specified field as an
	 * integer, if possible.
	 * 
	 * @param object The owner of the member field to be parsed
	 * @param field  The name of the member field to be parsed
	 * @return The integer value of the specified field, if possible.
	 * @throws SnapshotException     If a SnapshotException is thrown when resolving
	 *                               the field
	 * @throws NumberFormatException If the value could not be parsed as an integer
	 */
	public static int getInt(IObject object, String field) throws SnapshotException {
		Object obj = object.resolveValue(field);
		return Integer.valueOf(String.valueOf(obj));
	}

	/**
	 * Extracts the value of the member field by the specified name from the
	 * specified object as a byte array. If the member field does not exist, returns
	 * an empty byte array.
	 * 
	 * @param object The owner of the member field to be extracted
	 * @param field  The name of the member field to be extracted
	 * @return The value of the specified field as a byte array
	 * @throws SnapshotException        If a SnapshotException is thrown when
	 *                                  resolving the field
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static byte[] getByteArray(IObject object, String field) throws SnapshotException {
		IPrimitiveArray array = getIPrimitiveArray(object, field);
		return array == null ? new byte[0] : (byte[]) array.getValueArray();
	}

	/**
	 * Extracts the value of the member field by the specified name from the
	 * specified object as a byte array and converts it to a String. If the member
	 * field does not exist, returns an empty String.
	 * 
	 * @param object The owner of the member field to be extracted
	 * @param field  The name of the member field to be extracted
	 * @return The value of the specified field as a String
	 * @throws SnapshotException        If a SnapshotException is thrown when
	 *                                  resolving the field
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static String getByteArrayAsString(IObject object, String field) throws SnapshotException {
		return new String(getByteArray(object, field));
	}

	/**
	 * Extracts the value of the member field by the specified name from the
	 * specified object as a char array. If the member field does not exist, returns
	 * an empty char array.
	 * 
	 * @param object The owner of the member field to be extracted
	 * @param field  The name of the member field to be extracted
	 * @return The value of the specified field as a char array
	 * @throws SnapshotException        If a SnapshotException is thrown when
	 *                                  resolving the field
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static char[] getCharArray(IObject object, String field) throws SnapshotException {
		IPrimitiveArray array = getIPrimitiveArray(object, field);
		return array == null ? new char[0] : (char[]) array.getValueArray();
	}

	/**
	 * Extracts the value of the member field by the specified name from the
	 * specified object as a char array and converts it to a String. If the member
	 * field does not exist, returns an empty String.
	 * 
	 * @param object The owner of the member field to be extracted
	 * @param field  The name of the member field to be extracted
	 * @return The value of the specified field as a String
	 * @throws SnapshotException        If a SnapshotException is thrown when
	 *                                  resolving the field
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static String getCharArrayAsString(IObject object, String field) throws SnapshotException {
		return new String(getCharArray(object, field));
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
	public static boolean tryParseInt(Object object, Consumer<Integer> ifInt) {
		try {
			ifInt.accept(Integer.valueOf(String.valueOf(object)));
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * Extracts the value of the member field by the specified name from the
	 * specified object as an IPrimitiveArray. If the member field does not exist,
	 * returns null. If the member field is not an IPrimitiveArray, throws an
	 * IllegalArgumentException.
	 * 
	 * @param object The owner of the member field to be extracted
	 * @param field  The name of the member field to be extracted
	 * @return The value of the specified field as an IPrimitiveArray
	 * @throws SnapshotException        If a SnapshotException is thrown when
	 *                                  resolving the field
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	private static IPrimitiveArray getIPrimitiveArray(IObject object, String field) throws SnapshotException {
		Object value = object.resolveValue(field);
		if (value == null) {
			return null;
		}

		if (!(value instanceof IPrimitiveArray)) {
			throw new IllegalArgumentException("Field " + field + " on object " + object + " is not a primitive array");
		}

		return (IPrimitiveArray) value;
	}

}
