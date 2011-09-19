/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.constants;

public enum ResultType {
	HITS {
		@Override
		public String toString() {
			return "hits";
		}
	},
	RESULTS {
		@Override
		public String toString() {
			return "results";
		}
	}
}
