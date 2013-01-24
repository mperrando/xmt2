package com.pmease.commons.xmt.bean;

import java.util.Stack;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Bean5 extends Bean3 {

	private String loginName;

	private String firstName;

	private String lastName;

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@SuppressWarnings("unused")
	private void migrate1(Document dom, Stack<Integer> versions) {
		Element root = dom.getDocumentElement();
		Element element = (Element) root.getElementsByTagName("fullName").item(
				0);
		if (element != null) {
			String fullName = element.getTextContent();
			int index = fullName.indexOf(' ');
			if (index == -1) {
				root.appendChild(dom.createElement("firstName"))
						.setTextContent(fullName);
			} else {
				root.appendChild(dom.createElement("firstName"))
						.setTextContent(fullName.substring(0, index));
				root.appendChild(dom.createElement("lastName")).setTextContent(
						fullName.substring(index + 1, fullName.length() - 1));
			}
			root.removeChild(element);
		}
	}
}
