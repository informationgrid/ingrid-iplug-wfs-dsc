/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.tools;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


public class StringUtils {

	public static String join(Object[] parts, String separator) {
		StringBuilder str = new StringBuilder();
		for (Object part : parts) {
			str.append(part).append(separator);
		}
		if (str.length() > 0)
			return str.substring(0, str.length()-separator.length());

		return str.toString();
	}

	public static String nodeToString(Node node) {
		try {
			Source source = new DOMSource(node);
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// just set this to get literally equal results
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			transformer.transform(source, result);
			return stringWriter.getBuffer().toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static String extractNodeContent(Node node) {
		try {
			Source source = null;
			StringWriter stringWriter = new StringWriter();
			Result result = new StreamResult(stringWriter);
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			// extract node children
			NodeList nl = node.getChildNodes();
			for(int x=0, count=nl.getLength(); x<count; x++) {
				Node e = nl.item(x);
				if(e instanceof Element) {
					source = new DOMSource(e);
					transformer.transform(source, result);
				}
			}
			String docStr = stringWriter.getBuffer().toString();
			String content = docStr.replaceAll("^<[^<]+>", "").replaceAll("\\r|\\n|\\t", "");
			return content;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static Document stringToDocument(String string) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		InputSource inStream = new InputSource();
		inStream.setCharacterStream(new StringReader(string));
		Document doc = builder.parse(inStream);
		return doc;
	}

	public static String generateUuid() {
		UUID uuid = UUID.randomUUID();
		StringBuffer idcUuid = new StringBuffer(uuid.toString().toUpperCase());
		while (idcUuid.length() < 36) {
			idcUuid.append("0");
		}
		return idcUuid.toString();
	}
}
