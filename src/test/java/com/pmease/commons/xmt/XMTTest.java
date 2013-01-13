package com.pmease.commons.xmt;

import static com.pmease.commons.xmt.Util.readXML;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.pmease.commons.xmt.bean.Bean1;
import com.pmease.commons.xmt.bean.Bean1Migrator;
import com.pmease.commons.xmt.bean.Bean2;
import com.pmease.commons.xmt.bean.Bean3;
import com.pmease.commons.xmt.bean.Bean3Migrator;
import com.pmease.commons.xmt.bean.Bean4;
import com.pmease.commons.xmt.bean.Bean5;
import com.pmease.commons.xmt.bean.Bean7;
import com.pmease.commons.xmt.bean.CompileTask;
import com.pmease.commons.xmt.bean.SimpleBean1;
import com.pmease.commons.xmt.bean.SimpleBean1Migrator;

public class XMTTest {

	@Test
	public void testMigration() {
		Bean1 bean = (Bean1) VersionedDocument.fromXML(readXML("bean1.xml"))
				.toBean();
		assertEquals(bean.getPriority(), 10);
	}

	@Test
	public void testClassRenamedMigration() {
		Bean2 bean = (Bean2) VersionedDocument.fromXML(readXML("bean1.xml"))
				.toBean(Bean2.class);
		assertEquals(bean.getPriority(), 10);
	}

	@Test
	public void testCompositeVersion() {
		assertEquals(MigrationHelper.getVersion(Bean3.class), "1.3");
	}

	@Test
	public void testNoVersion() {
		assertEquals(MigrationHelper.getVersion(Bean4.class), "0");
	}

	@Test
	public void testCompositeMigration() {
		Bean5 bean = (Bean5) VersionedDocument.fromXML(readXML("bean2.xml"))
				.toBean();
		assertEquals(bean.getPriority(), 10);
		assertEquals(bean.getFirstName(), "Robin");
	}

	@Test
	public void testAddClass() {
		Bean7 bean = (Bean7) VersionedDocument.fromXML(readXML("bean2.xml"))
				.toBean(Bean7.class);
		assertEquals(bean.getPriority(), 10);
		assertEquals(bean.getFirstName(), "Robin");
		assertEquals(bean.getAge(), 34);
	}

	@Test
	public void testRemoveClass() {
		CompileTask task = (CompileTask) VersionedDocument.fromXML(
				readXML("task.xml")).toBean();
		assertEquals(task.priority, 10);
		assertEquals(task.destDir, "classes");
		assertEquals(task.options, "-debug");
		assertEquals(task.srcFiles.size(), 2);
	}

	@Test
	public void testMigrationWithMigratorProvider() {
		SimpleMigratorProvider migratorProvider = new SimpleMigratorProvider();
		migratorProvider.put(Bean1.class, Bean1Migrator.class);
		Bean1 bean = (Bean1) VersionedDocument.fromXML(readXML("bean1.xml"),
				migratorProvider).toBean();
		assertEquals(bean.getPriority(), 11);
	}

	@Test
	public void testGetVersionWithMigrator() {
		SimpleMigratorProvider migratorProvider = new SimpleMigratorProvider();
		migratorProvider.put(Bean1.class, Bean1Migrator.class);
		assertEquals(MigrationHelper.getVersion(Bean1.class, migratorProvider),
				"2");
	}

	@Test
	public void testCompositeVersionWithMigrator() {
		SimpleMigratorProvider migratorProvider = new SimpleMigratorProvider();
		migratorProvider.put(Bean1.class, Bean1Migrator.class);
		migratorProvider.put(Bean3.class, Bean3Migrator.class);
		assertEquals(MigrationHelper.getVersion(Bean3.class, migratorProvider),
				"2.5");
	}

	@Test
	public void testMigrationlInheritedWithMigrator() {
		SimpleMigratorProvider migratorProvider = new SimpleMigratorProvider();
		migratorProvider.put(Bean1.class, Bean1Migrator.class);
		migratorProvider.put(Bean3.class, Bean3Migrator.class);
		Bean3 bean = (Bean3) VersionedDocument.fromXML(readXML("bean1.xml"),
				migratorProvider).toBean(Bean3.class);
		assertEquals(bean.getPriority(), 11);
		assertEquals(bean.getAge(), 18);
	}

	@Test
	public void testMigrationWithStream() throws IOException {
		InputStream is = Util.readXMLAsStream("bean1.xml");
		try {
			Bean1 bean = (Bean1) VersionedDocument.fromStream(is).toBean();
			assertEquals(bean.getPriority(), 10);
		} finally {
			is.close();
		}
	}

	@Test(expected = Exception.class)
	public void testNoMigrationWithoutVersionInXml() {
		VersionedDocument.fromXML(readXML("bean1_no_version.xml")).toBean();
	}

	@Test
	public void testMigrationFromVersionZeroWithoutVersionInXml() {
		VersionedDocument.assumeVersionZeroForNoVersionedBeans = true;
		Bean1 bean = (Bean1) VersionedDocument.fromXML(readXML("bean1.xml"))
				.toBean();
		assertEquals(bean.getPriority(), 10);
	}

	@Test
	public void testVersionNumberFromBean() {
		Bean1 bean1 = new Bean1();
		VersionedDocument document = VersionedDocument.fromBean(bean1);
		assertEquals("1", document.getVersion());
	}

	@Test
	public void testVersionNumberFromBeanWithMigrator() {
		SimpleBean1 b = new SimpleBean1();
		SimpleMigratorProvider migratorProvider = new SimpleMigratorProvider();
		migratorProvider.put(SimpleBean1.class, SimpleBean1Migrator.class);
		VersionedDocument document = VersionedDocument.fromBean(b, migratorProvider);
		assertEquals("1", document.getVersion());
	}
}
