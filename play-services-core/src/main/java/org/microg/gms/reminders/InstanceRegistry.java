package org.microg.gms.reminders;


import java.util.HashMap;
import java.util.Map;

public class InstanceRegistry {
	private static Map<Class<?>, Object> instanceMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T getInstance(Class<T> clazz) {
		synchronized (InstanceRegistry.class) {
			Object instance = instanceMap.get(clazz);
			if (instance == null) {
				try {
					instance = clazz.getDeclaredConstructor().newInstance();
					instanceMap.put(clazz, instance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return (T) instance;
		}
	}
}
