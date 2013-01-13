package com.pmease.commons.xmt;

/**
 * User that want to use the Migrationprovider approach must pass a
 * MigratioProvider object to to VersionedDocument.
 * 
 * When a MigrationProvider is used, the migration method are non more looked
 * for into the bean class but are serached in the mgirator class.
 * 
 * The class returned, that is the migrator calss, must have the migration
 * method as specified in the documentation.
 * 
 * IMPORTANT! if BeanB extends BeanA, then this
 * 
 * @author marco.perrando@gmail.com
 */
public interface MigratorProvider {
	/**
	 * Returns a class which methods must conform to the xmt specification which
	 * must migrate data for a bean of a given class.
	 * 
	 * @param beanClass
	 *            the class of bean for which migration is required.
	 * @return the class of the migrator that will perform the migration for the
	 *         bean.
	 */
	Class<?> get(Class<?> beanClass);
}
