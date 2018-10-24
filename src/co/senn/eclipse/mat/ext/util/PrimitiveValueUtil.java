package co.senn.eclipse.mat.ext.util;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;

/**
 * A utility class for extracting primitive values from {@linkplain IObject heap
 * objects}.
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
	 * @throws SnapshotException
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
	 * @throws SnapshotException
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static byte[] getByteArray(IObject object, String field) throws SnapshotException {
		IPrimitiveArray array = getIPrimitiveArray(object, field);
		return array == null ? new byte[0] : (byte[]) array.getValueArray();
	}

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
	 * @throws SnapshotException
	 * @throws IllegalArgumentException If the field by the specified name is not a
	 *                                  {@linkplain IPrimitiveArray primitive array}
	 */
	public static char[] getCharArray(IObject object, String field) throws SnapshotException {
		IPrimitiveArray array = getIPrimitiveArray(object, field);
		return array == null ? new char[0] : (char[]) array.getValueArray();
	}

	public static String getCharArrayAsString(IObject object, String field) throws SnapshotException {
		return new String(getCharArray(object, field));
	}

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
