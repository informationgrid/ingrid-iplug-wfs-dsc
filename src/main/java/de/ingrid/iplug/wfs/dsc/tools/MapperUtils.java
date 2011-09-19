/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.tools;

import de.ingrid.utils.dsc.Column;

/**
 * @author Administrator
 *
 */
public class MapperUtils {

	public static Column createColumn(String tableName, String columnName, String targetName, String type, boolean indexColumn) {
		Column c = new Column(tableName, columnName, type, indexColumn);
		c.setTargetName(targetName);
		return c;
	}


}
