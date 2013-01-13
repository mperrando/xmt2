package com.pmease.commons.xmt;

import java.util.HashMap;

/**
 * A MigrationProvider implementation simply backed by a Java Map.
 * 
 * @author marco.perrando@gmail.com
 */
public class SimpleMigratorProvider implements MigratorProvider {

	private HashMap<Class<?>, Class<?>> migrators = new HashMap<Class<?>, Class<?>>();

	@Override
	public Class<?> get(Class<?> beanClass) {
		return migrators.get(beanClass);
	}

	public Class<?> put(Class<?> beanClass, Class<?> migratorClass) {
		return migrators.put(beanClass, migratorClass);
	}
}
