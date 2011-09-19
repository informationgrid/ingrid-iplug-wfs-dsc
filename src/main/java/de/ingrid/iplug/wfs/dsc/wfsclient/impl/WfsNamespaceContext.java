/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.wfsclient.impl;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import de.ingrid.iplug.wfs.dsc.wfsclient.constants.Namespace;

/**
 * @author ingo@wemove.com
 */
enum WfsNamespaceContext implements NamespaceContext {

	// Guaranteed to be the single instance
	INSTANCE;

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		for (Namespace ns : Namespace.values()) {
			QName qName = ns.getQName();
			if (qName.getPrefix().equals(prefix)) {
				return qName.getNamespaceURI();
			}
		}
		return XMLConstants.NULL_NS_URI;
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	@Override
	public Iterator<String> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

}
