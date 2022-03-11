/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.tools;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author joachim@wemove.com
 * 
 */
public class NodeUtils {

	/**
	 * Detaches the node from it's document. The node will be
	 * wrapped in a <code>DocumentFragment</code>.
	 * 
	 * It also copies all name space definitions from it's parent nodes into the
	 * node as new attributes.
	 * 
	 * @param node
	 *            The node to be detached from it's document.
	 * @return The detached node.
	 */
	public static Node detachWithNameSpaces(Node node) {
		Element clone = (Element) node.cloneNode(true);
		DocumentFragment docFragment = node.getOwnerDocument().createDocumentFragment();
		docFragment.appendChild(clone);
		Node parentNode = node.getParentNode();
		while (parentNode != null) {
			if (parentNode.getAttributes() != null) {
				for (int i = 0; i < parentNode.getAttributes().getLength(); i++) {
					Node attribute = parentNode.getAttributes().item(i);
					if (attribute.getNodeName().toLowerCase().startsWith("xmlns:")
							|| attribute.getNodeName().equalsIgnoreCase("xmlns")) {
						clone.setAttribute(attribute.getNodeName(), attribute.getNodeValue());
					}
				}
			}
			parentNode = parentNode.getParentNode();
		}
		return clone;
	}
}
