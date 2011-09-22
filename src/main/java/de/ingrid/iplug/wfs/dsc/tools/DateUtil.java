package de.ingrid.iplug.wfs.dsc.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public final static SimpleDateFormat outFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	public final static SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public static String formatDate(String dateStr) {
		try {
			// get rid of colon in time zone (2011-09-19T15:15:00+01:00)
			dateStr = dateStr.replaceFirst(":([0..9]{2})$", "$1");

			// convert
			Date date = inFormat.parse(dateStr);
			return outFormat.format(date);
		}
		catch (Exception e) {
			return "";
		}
	}
}
