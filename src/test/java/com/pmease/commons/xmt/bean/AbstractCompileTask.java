package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.pmease.commons.xmt.MigrationHelper;

public class AbstractCompileTask {
	public int priority;

	public String options = "-debug";

	@SuppressWarnings("unused")
	private void migrate1(Document dom, Stack<Integer> versions) {
		int taskVersion = versions.pop();
		MigrationHelper.migrate(String.valueOf(taskVersion),
				TaskMigrator.class, dom);
	}

	public static class TaskMigrator {

		@SuppressWarnings("unused")
		private void migrate1(Document dom, Stack<Integer> versions) {
			Node node = dom.getDocumentElement().getElementsByTagName("prioritized").item(0);
			Element element = (Element) node;
			element = (Element) dom.renameNode(node, "", "priority");
			if (element.getTextContent().equals("true"))
				element.setTextContent("HIGH");
			else
				element.setTextContent("LOW");
		}

		@SuppressWarnings("unused")
		private void migrate2(Document dom, Stack<Integer> versions) {
			Element element = (Element) dom.getDocumentElement().getElementsByTagName("priority").item(0);
			String content = element.getTextContent();
			if (content.equals("HIGH"))
				element.setTextContent("10");
			else if (content.equals("MEDIUM"))
				element.setTextContent("5");
			else
				element.setTextContent("1");
		}
	}
}