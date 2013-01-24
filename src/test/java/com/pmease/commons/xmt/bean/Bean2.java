package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Bean2 {
	private int priority;

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	@SuppressWarnings("unused")
	private void migrate1(Document dom, Stack<Integer> versions) {
		Node node = dom.getDocumentElement().getElementsByTagName("prioritized").item(0);
		Element element = (Element) node;
		element = (Element) dom.renameNode(element, "", "priority");
		if (element.getTextContent().equals("true"))
			element.setTextContent("10");
		else
			element.setTextContent("1");
	}
}
