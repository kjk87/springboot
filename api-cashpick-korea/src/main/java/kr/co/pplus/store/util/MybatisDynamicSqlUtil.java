package kr.co.pplus.store.util;

import java.util.Collection;
import java.util.Map;

public class MybatisDynamicSqlUtil {
	public static boolean isEmpty(Object o) {
		if (o == null)
			return true;
		if (o instanceof String) {
			if (((String) o).trim().length() > 0)
				return false;
			else
				return true;
		} else if (o instanceof Collection<?>) {
			if (((Collection<?>) o).size() > 0)
				return false;
			else
				return true;
		} else if (o instanceof Map<?,?>) {
			if (((Map<String,Object>) o).size() > 0)
				return false;
			else
				return true;

		}
		return false;
	}
	
	public static boolean isEmpty(Object[] ar) {
		if (ar == null || ar.length < 1)
			return true;
		else {
			for (Object o : ar) {
				if (!isEmpty(o)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}
	
	public static boolean isNotEmpty(Object[] ar) {
		return !isEmpty(ar);
	}
	
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}
	
}
