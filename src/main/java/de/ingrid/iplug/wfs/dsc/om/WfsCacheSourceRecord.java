/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.om;

import de.ingrid.iplug.wfs.dsc.wfsclient.WFSFeature;

/**
 * Represents a record from a wfs cache.
 * 
 * @author joachim@wemove.com
 * 
 */
public class WfsCacheSourceRecord extends SourceRecord {

	private static final long serialVersionUID = 5660303708840795055L;

	public static final String WFS_RECORD = "wfsRecord";

	/**
	 * Creates a WfsCacheSourceRecord. It holds the source record id and the
	 * cache for further usage.
	 * 
	 * @param id
	 * @param connection
	 */
	public WfsCacheSourceRecord(WFSFeature record) {
		super(record.getId());
		this.put(WFS_RECORD, record);
	}

}
