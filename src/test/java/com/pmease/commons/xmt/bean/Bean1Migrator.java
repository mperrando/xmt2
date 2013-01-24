package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Bean1Migrator {
	@SuppressWarnings("unused")
	private void migrate2(Document dom, Stack<Integer> versions) {
		Node node = dom.getDocumentElement().getElementsByTagName("prioritized").item(0);
		Element element = (Element) node;
		element = (Element) dom.renameNode(node, "", "priority");		
		String content = element.getTextContent();
		if (content.equals("true"))
			element.setTextContent("11");
		else
			element.setTextContent("1");
	}

}
