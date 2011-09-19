/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.record.mapper;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.iplug.wfs.dsc.om.SourceRecord;
import de.ingrid.iplug.wfs.dsc.om.WfsCacheSourceRecord;
import de.ingrid.iplug.wfs.dsc.tools.DocumentStyler;
import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Creates a base InGrid Detail data Format (IDF) skeleton.
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsIdfMapper implements IIdfMapper {

	protected static final Logger log = Logger.getLogger(WfsIdfMapper.class);
	protected static final XPathUtils xPathUtils = new XPathUtils(new IDFNamespaceContext());

	private Resource styleSheetResource;

	@Override
	public void map(SourceRecord record, Document doc) throws Exception {

		if (!(record instanceof WfsCacheSourceRecord)) {
			log.error("Source Record is not a WfsCacheSourceRecord!");
			throw new IllegalArgumentException(
					"Source Record is not a WfsCacheSourceRecord!");
		}

		WFSFeature wfsRecord = (WFSFeature) record
				.get(WfsCacheSourceRecord.WFS_RECORD);

		Node body = xPathUtils.getNode(doc, "/idf:html/idf:body");
		Node originalResponse = wfsRecord.getOriginalResponse();
		Source style = new StreamSource(this.styleSheetResource.getInputStream());
		DocumentStyler ds = new DocumentStyler(style);

		Document idfResponse = ds.transform(originalResponse.getOwnerDocument());
		Node wfs = doc.importNode(idfResponse.getDocumentElement(), true);

		body.appendChild(wfs);
	}

	public Resource getStyleSheetResource() {
		return this.styleSheetResource;
	}

	public void setStyleSheetResource(Resource styleSheetResource) {
		this.styleSheetResource = styleSheetResource;
	}

}
