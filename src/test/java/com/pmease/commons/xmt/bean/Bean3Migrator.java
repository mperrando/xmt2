package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Bean3Migrator {
	@SuppressWarnings("unused")
	private void migrate5(Document dom, Stack<Integer> versions) {
		Element element = (Element) dom.getDocumentElement().appendChild(dom.createElement("age"));
		element.setTextContent("18");
	}
}
