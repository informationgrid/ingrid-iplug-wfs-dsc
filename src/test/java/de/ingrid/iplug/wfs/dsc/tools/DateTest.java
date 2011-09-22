package de.ingrid.iplug.wfs.dsc.tools;

import junit.framework.TestCase;

public class DateTest extends TestCase {

	public void testFormat() throws Exception {
		String input = "2011-09-19T15:15:00+01:00";
		String output = "19.09.2011 16:15:00";

		assertEquals(output, DateUtil.formatDate(input));
	}
}
