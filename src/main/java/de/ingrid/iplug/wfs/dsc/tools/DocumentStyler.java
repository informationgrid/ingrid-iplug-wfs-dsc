/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.tools;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import org.w3c.dom.Document;

/**
 * @author joachim
 * 
 */
public class DocumentStyler {
	private Transformer transformer;

	public DocumentStyler(Source aStyleSheet) throws Exception {
		// create transformer
		TransformerFactory factory = TransformerFactory.newInstance();
		this.transformer = factory.newTransformer(aStyleSheet);
	}

	public Document transform(Document aDocument) throws Exception {

		// perform transformation
		DOMSource source = new DOMSource(aDocument);
		DOMResult result = new DOMResult();
		this.transformer.transform(source, result);

		// return resulting document
		return (Document) result.getNode();
	}
}
