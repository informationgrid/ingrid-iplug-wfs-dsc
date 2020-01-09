/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2020 wemove digital solutions GmbH
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
