package kr.co.pplus.store.type.model;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public abstract class AbstractModel implements Model {
	
	protected static String DATE_FORMAT =  "yyyy-MM-dd HH:mm:ss";
	
	protected static String[] primitiveTypeNames = {
		"java.lang.String"
		, "java.lang.Integer"
		, "java.lang.Boolean"
		, "java.lang.Long"
		, "java.lang.Byte"
		, "java.lang.Short"
		, "java.lang.Double"
		, "java.lang.Float"
		, "java.util.Date"
	};
	
	
	
	public static boolean isPrimitive(Class<?> clazz) {
		if (clazz.isPrimitive())
			return true;
		
		try {
			for (String name : AbstractModel.primitiveTypeNames) {
				StringBuffer sb = new StringBuffer();
				Class<?> declaringClass = clazz.getDeclaringClass();
				if (declaringClass != null)
					sb.append(declaringClass.getName()+ ".");
				sb.append(clazz.getName());
				if (name.equalsIgnoreCase(sb.toString()))
					return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	protected static Object convertValue(Class<?> srcClass, Class<?> destClass, Object value, String dateFormat) {
		if (String.class.isAssignableFrom(destClass)) {
			if (java.util.Date.class.isAssignableFrom(srcClass))
				return new SimpleDateFormat(dateFormat).format(value);
			else
				return value.toString();
		} else if (String.class.isAssignableFrom(srcClass)) {
			if (Integer.class.isAssignableFrom(destClass))
				return Integer.parseInt((String)value);
			else if (Long.class.isAssignableFrom(destClass))
				return Long.parseLong((String)value);
			else if (Boolean.class.isAssignableFrom(destClass))
				return Boolean.parseBoolean((String)value);
			else if (Byte.class.isAssignableFrom(destClass))
				return Byte.parseByte((String)value);
			else if (Short.class.isAssignableFrom(destClass))
				return Short.parseShort((String)value);
			else if (Double.class.isAssignableFrom(destClass))
				return Double.parseDouble((String)value);
			else if (Float.class.isAssignableFrom(destClass))
				return Float.parseFloat((String)value);
			else if (java.util.Date.class.isAssignableFrom(destClass)) {
				try {
					return new SimpleDateFormat(dateFormat).parse((String)value);
				} catch (ParseException ex) {
					return null;
				}
			}
		} else if (Integer.class.isAssignableFrom(srcClass)) {
			if (Long.class.isAssignableFrom(destClass))
				return ((Integer)value).longValue();
			else if (Byte.class.isAssignableFrom(destClass))
				return ((Integer)value).byteValue();
			else if (Short.class.isAssignableFrom(destClass))
				return ((Integer)value).shortValue();
			else if (Double.class.isAssignableFrom(destClass))
				return ((Integer)value).doubleValue();
			else if (Float.class.isAssignableFrom(destClass))
				return ((Integer)value).floatValue();
		} else if (Short.class.isAssignableFrom(srcClass)) {
			if (Long.class.isAssignableFrom(destClass))
				return ((Short)value).longValue();
			else if (Byte.class.isAssignableFrom(destClass))
				return ((Short)value).byteValue();
			else if (Integer.class.isAssignableFrom(destClass))
				return ((Short)value).intValue();
			else if (Double.class.isAssignableFrom(destClass))
				return ((Short)value).doubleValue();
			else if (Float.class.isAssignableFrom(destClass))
				return ((Short)value).floatValue();
		} else if (Long.class.isAssignableFrom(srcClass)) {
			if (Short.class.isAssignableFrom(destClass))
				return ((Long)value).shortValue();
			else if (Byte.class.isAssignableFrom(destClass))
				return ((Long)value).byteValue();
			else if (Integer.class.isAssignableFrom(destClass))
				return ((Long)value).intValue();
			else if (Double.class.isAssignableFrom(destClass))
				return ((Long)value).doubleValue();
			else if (Float.class.isAssignableFrom(destClass))
				return ((Long)value).floatValue();
		} else if (Byte.class.isAssignableFrom(srcClass)) {
			if (Short.class.isAssignableFrom(destClass))
				return ((Byte)value).shortValue();
			else if (Long.class.isAssignableFrom(destClass))
				return ((Byte)value).longValue();
			else if (Integer.class.isAssignableFrom(destClass))
				return ((Byte)value).intValue();
			else if (Double.class.isAssignableFrom(destClass))
				return ((Byte)value).doubleValue();
			else if (Float.class.isAssignableFrom(destClass))
				return ((Byte)value).floatValue();
		} else if (Double.class.isAssignableFrom(srcClass)) {
			if (Short.class.isAssignableFrom(destClass))
				return ((Double)value).doubleValue();
			else if (Long.class.isAssignableFrom(destClass))
				return ((Double)value).longValue();
			else if (Integer.class.isAssignableFrom(destClass))
				return ((Double)value).intValue();
			else if (Byte.class.isAssignableFrom(destClass))
				return ((Double)value).byteValue();
			else if (Float.class.isAssignableFrom(destClass))
				return ((Double)value).floatValue();
		} else if (Float.class.isAssignableFrom(srcClass)) {
			if (Short.class.isAssignableFrom(destClass))
				return ((Double)value).doubleValue();
			else if (Long.class.isAssignableFrom(destClass))
				return ((Double)value).longValue();
			else if (Integer.class.isAssignableFrom(destClass))
				return ((Double)value).intValue();
			else if (Byte.class.isAssignableFrom(destClass))
				return ((Double)value).byteValue();
			else if (Double.class.isAssignableFrom(destClass))
				return ((Double)value).doubleValue();
		}
		return value;
	}
	
	protected Field getDeclaredField(Class<?> clazz, String key) throws Exception {
		try {
			Field fld = clazz.getDeclaredField(key);
			return fld;
		} catch (NoSuchFieldException ex) {
			Class<?> s = clazz.getSuperclass();
			if (s != null) 
				return getDeclaredField(s, key);
			else 
				throw ex;
			
		} catch (SecurityException ex) {
			throw ex;
		}
	}	

	@Override
	public Map<String, Object> toMap(Map dest, boolean excludeNull) {
		if (dest == null)
			dest = new HashMap<String, Object>();
		
		Class<?> cls = this.getClass();
		try {
			for (Method m : this.getClass().getMethods()) {
				try {
					String name = m.getName();
					String key = null;
					
					if (name.startsWith("is") && m.getParameterTypes().length == 0) {
						key = name.substring(2, 3).toLowerCase() + name.substring(3);
						dest.put(key, m.invoke(this));
					} else if (name.startsWith("get")  && m.getParameterTypes().length == 0) {
						key = name.substring(3, 4).toLowerCase() + name.substring(4);
						Class valueClass = m.getReturnType();
						Object value = m.invoke(this);
						
						if (excludeNull && value == null) 
							continue;
						
						if (AbstractModel.isPrimitive(valueClass) || Map.class.isAssignableFrom(valueClass)) {
							dest.put(key, AbstractModel.convertValue(valueClass, valueClass, value, AbstractModel.DATE_FORMAT));
						} else if (List.class.isAssignableFrom(valueClass)) {
							Field fld = getDeclaredField(cls, key);
							ParameterizedType paramType = (ParameterizedType)fld.getGenericType();
							Class paramTypeClass = (Class)paramType.getActualTypeArguments()[0];
							if (value != null) {
								List l = new ArrayList();
								for (Object obj : (List)value) {
									if (AbstractModel.class.isAssignableFrom(paramTypeClass)) {
										l.add(((AbstractModel)obj).toMap(null, excludeNull));
									} else {
										l.add(obj);
									}
								}
								
								if (l.size() > 0) {
									dest.put(key, l);
								}
							}
					
						} else if (AbstractModel.class.isAssignableFrom(valueClass) && value != null) {
							dest.put(key, ((AbstractModel)value).toMap(null, excludeNull));
						} /*else if (Map.class.isAssignableFrom(valueClass)) {
							Map map = new HashMap<String, Object>();
							if (value != null) {
								Map<String, Object> tmpMap = (Map<String, Object>)value;
								for (Map.Entry<String, Object> entry : tmpMap.entrySet()) {
									String k = entry.getKey();
									Object v = entry.getValue();
									if (v != null) {
										if (AbstractModel.class.isAssignableFrom(v.getClass())) {
											map.put(k, ((AbstractModel)v).toMap(null, excludeNull));
										} else if () {
											
										}
									}
								}
							}
							dest.put(key, map);
						}*/
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} 
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return dest;
	}

	@Override
	public Model fromMap(Map<String, Object> src, boolean excludeNull) {
		if (src == null)
			return this;
		
		
		Class<?> cls = this.getClass();
		
		try {
			for (Method m : this.getClass().getMethods()) {
				try {
					String name = m.getName();
					if (name.startsWith("set")) {
						boolean containsKey = false;
						String key = name.substring(3, 4).toLowerCase() + name.substring(4);
						if (src.containsKey(name.substring(3, 4).toLowerCase() + name.substring(4))) {
							containsKey = true;
						} else if (src.containsKey(name.substring(3, 4).toUpperCase() + name.substring(4))) {
							containsKey = true;
							key = name.substring(3, 4).toUpperCase() + name.substring(4);
						}
						
						if (containsKey) {
							Object value = src.get(key);
							
							if (excludeNull && value == null)
								continue;

							Class valueClass = value.getClass();
							
							
							if (AbstractModel.isPrimitive(valueClass)) {
								Class paramType = m.getParameterTypes()[0];
								try {
									m.invoke(this, AbstractModel.convertValue(valueClass, paramType, value, AbstractModel.DATE_FORMAT));
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							} else if (List.class.isAssignableFrom(valueClass)) {
								try {
									Field fld = getDeclaredField(cls, key);
									ParameterizedType paramType = (ParameterizedType)fld.getGenericType();
									Class paramTypeClass = (Class)paramType.getActualTypeArguments()[0];
									if (value != null) {
										List l = new ArrayList();
										if (AbstractModel.isPrimitive(paramTypeClass)) {
											for (Object item : (List)value) {
												l.add(item);
											}
										} else if (AbstractModel.class.isAssignableFrom(paramTypeClass)) {
											for (Object item : (List)value) {
												if (Map.class.isAssignableFrom(item.getClass())) {
													AbstractModel dto = (AbstractModel)paramTypeClass.newInstance();
													dto.fromMap((Map<String, Object>)item, excludeNull);
													l.add(dto);
												}
											}
										}
										m.invoke(this, l);
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							} else if (Map.class.isAssignableFrom(valueClass)) {
								Class paramType = m.getParameterTypes()[0];
								if (AbstractModel.class.isAssignableFrom(paramType)) {
									AbstractModel dto = (AbstractModel)paramType.newInstance();
									dto.fromMap((Map<String, Object>)value, excludeNull);
									m.invoke(this, dto);
								}
							} else {
								m.invoke(this, value);
							}
							
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				} 
			}
			return this;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			
		}
		return this;
	}

}
