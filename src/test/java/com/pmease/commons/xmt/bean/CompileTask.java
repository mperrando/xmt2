package com.pmease.commons.xmt.bean;

import java.util.List;
import java.util.Stack;

import org.w3c.dom.Document;

public class CompileTask extends AbstractCompileTask {
	public List<String> srcFiles;

	public String destDir = "classes";

	@SuppressWarnings("unused")
	private void migrate1(Document dom, Stack<Integer> versions) {
		dom.getDocumentElement().appendChild(dom.createElement("destDir")).setTextContent("classes");
	}

	@SuppressWarnings("unused")
	private void migrate2(Document dom, Stack<Integer> versions) {
		versions.push(0);
		dom.getDocumentElement().appendChild(dom.createElement("options")).setTextContent("-debug");
	}
}