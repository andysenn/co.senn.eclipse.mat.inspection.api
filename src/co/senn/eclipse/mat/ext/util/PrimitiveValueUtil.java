package co.senn.eclipse.mat.ext.util;

import org.eclipse.mat.SnapshotException;
import org.eclipse.mat.snapshot.model.IObject;
import org.eclipse.mat.snapshot.model.IPrimitiveArray;

public final class PrimitiveValueUtil {

	private PrimitiveValueUtil() {
	}

	public static int getInt(IObject object, String field) throws SnapshotException {
		Object obj = object.resolveValue(field);
		return Integer.valueOf(String.valueOf(obj));
	}

	public static byte[] getByteArray(IObject object, String field) throws SnapshotException {
		IPrimitiveArray array = getIPrimitiveArray(object, field);
		return array == null ? new byte[0] : (byte[]) array.getValueArray();
	}

	public static String getByteArrayAsString(IObject object, String field) throws SnapshotException {
		return new String(getByteArray(object, field));
	}

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
