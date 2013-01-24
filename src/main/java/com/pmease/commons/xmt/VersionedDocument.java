package com.pmease.commons.xmt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.xml.DomReader;
import com.thoughtworks.xstream.io.xml.DomWriter;

/**
 * This class is the bridge between bean and XML. It implements dom4j
 * Documentation interface and can be operated with dom4j API. It also
 * implements Serializable interface and can be serialized/deserialized in XML
 * form.
 * 
 * @author robin, marco.perrando@gmail.com
 * 
 */
public final class VersionedDocument {

	private final static DocumentBuilderFactory dbf = DocumentBuilderFactory
			.newInstance();
	private static final TransformerFactory tFactory = TransformerFactory
			.newInstance();

	public static XStream xstream = new XStream();

	public static boolean assumeVersionZeroForNoVersionedBeans;

	private final MigratorProvider migratorProvider;

	private final Document dom;

	// static {
	// reader.setStripWhitespaceText(true);
	// reader.setMergeAdjacentText(true);
	// }

	public VersionedDocument(Document dom, MigratorProvider migratorProvider) {
		this.dom = dom;
		this.migratorProvider = migratorProvider;
	}

	/**
	 * Convert the versioned document to UTF8 encoded XML.
	 * 
	 * @return
	 */
	public String toXML() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			toStream(baos);
			return baos.toString("UTF8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void toStream(OutputStream os) throws IOException {
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(dom);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new IOException("Problems with transofrmation", e);
		}
	}

	/**
	 * Construct the document from a XML text.
	 * 
	 * @param xml
	 *            UTF8 encoded XML text
	 * @return
	 */
	public static VersionedDocument fromXML(String xml) {
		return fromXML(xml, null);
	}

	public static VersionedDocument fromXML(String xml,
			MigratorProvider migratorProvider) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(
					xml.getBytes("UTF8"));
			return fromStream(bais, migratorProvider);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static VersionedDocument fromStream(InputStream is) {
		return fromStream(is, null);
	}

	public static VersionedDocument fromStream(InputStream is,
			MigratorProvider migratorProvider) {
		Document dom;
		try {
			dom = dbf.newDocumentBuilder().parse(is);
		} catch (Exception e) {
			throw newUnexpectedError(e);
		}
		return new VersionedDocument(dom, migratorProvider);

	}

	/**
	 * Construct the versioned document from specified bean object.
	 * 
	 * @param bean
	 * @return
	 */
	public static VersionedDocument fromBean(Object bean) {
		return fromBean(bean, null);
	}

	public static VersionedDocument fromBean(Object bean,
			MigratorProvider migratorProvider) {
		Document dom;
		try {
			dom = dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw newUnexpectedError(e);
		}
		xstream.marshal(bean, new DomWriter(dom));
		VersionedDocument versionedDom = new VersionedDocument(dom,
				migratorProvider);
		if (bean != null)
			versionedDom.setVersion(MigrationHelper.getVersion(bean.getClass(),
					migratorProvider));
		return versionedDom;
	}

	private static RuntimeException newUnexpectedError(Exception e) {
		return new RuntimeException("Unexpected error", e);
	}

	/**
	 * Convert this document to bean. Migration will performed if necessary.
	 * During the migration, content of the document will also get updated to
	 * reflect current migration result.
	 * 
	 * @return
	 */
	public Object toBean() {
		return toBean(null, null);
	}

	/**
	 * Convert this document to bean. Migration will performed if necessary.
	 * During the migration, content of the document will also get updated to
	 * reflect current migration result.
	 * 
	 * @param listener
	 *            the migration listener to receive migration events. Set to
	 *            null if you do not want to receive migration events.
	 * @return
	 */
	public Object toBean(MigrationListener listener) {
		return toBean(listener, null);
	}

	/**
	 * Convert this document to bean. Migration will performed if necessary.
	 * During the migration, content of the document will also get updated to
	 * reflect current migration result.
	 * 
	 * @param beanClass
	 *            class of the bean. Class information in current document will
	 *            be used if this param is set to null
	 * @return
	 */
	public Object toBean(Class<?> beanClass) {
		return toBean(null, beanClass);
	}

	/**
	 * Convert this document to bean. Migration will performed if necessary.
	 * During the migration, content of the document will also get updated to
	 * reflect current migration result.
	 * 
	 * @param listener
	 *            the migration listener to receive migration events. Set to
	 *            null if you do not want to receive migration events.
	 * @param beanClass
	 *            class of the bean. Class information in current document will
	 *            be used if this param is set to null
	 * @return
	 */
	public Object toBean(MigrationListener listener, Class<?> beanClass) {
		Class<?> origBeanClass = HierarchicalStreams.readClassType(
				new DomReader(dom), xstream.getMapper());

		if (origBeanClass == null)
			return null;
		if (beanClass == null)
			beanClass = origBeanClass;
		else
			dom.renameNode(dom.getDocumentElement(), "", xstream.getMapper()
					.serializedClass(beanClass));

		String version = getVersion();
		if (version == null && assumeVersionZeroForNoVersionedBeans)
			version = "0";
		if (version != null) {
			if (MigrationHelper.migrate(version, beanClass, migratorProvider,
					dom)) {
				setVersion(MigrationHelper.getVersion(beanClass,
						migratorProvider));
				Object bean = xstream.unmarshal(new DomReader(dom));
				if (listener != null)
					listener.migrated(bean);
				return bean;
			}
		}
		return xstream.unmarshal(new DomReader(dom));
	}

	/**
	 * Get version of the document
	 * 
	 * @return
	 */
	public String getVersion() {
		return dom.getDocumentElement().getAttribute("version");
	}

	/**
	 * Set version of the document
	 * 
	 * @param version
	 */
	public void setVersion(String version) {
		dom.getDocumentElement().setAttribute("version", version);
	}
}
