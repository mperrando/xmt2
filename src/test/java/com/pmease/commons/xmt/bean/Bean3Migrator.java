package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.dom4j.Element;

import com.pmease.commons.xmt.VersionedDocument;

public class Bean3Migrator {
	@SuppressWarnings("unused")
	private void migrate5(VersionedDocument dom, Stack<Integer> versions) {
		Element element = dom.getRootElement().addElement("age");
		element.setText("18");
	}
}
