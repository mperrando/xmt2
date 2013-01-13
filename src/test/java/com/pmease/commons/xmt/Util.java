package com.pmease.commons.xmt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Util {
	public static String readXML(String fileName) {
		StringBuffer buffer = new StringBuffer();
		InputStream is = null;
		is = readXMLAsStream(fileName);
		try {
			Reader in = new InputStreamReader(is, "UTF8");
			int c;
			while ((c = in.read()) != -1)
				buffer.append((char) c);
			return buffer.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static InputStream readXMLAsStream(String fileName) {
		return Util.class.getResourceAsStream("/com/pmease/commons/xmt/xml/"
				+ fileName);
	}
}
