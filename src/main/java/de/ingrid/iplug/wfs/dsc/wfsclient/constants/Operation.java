/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.iplug.wfs.dsc.wfsclient.constants;

public enum Operation {
	/**
	 * OGC_Service interface
	 */
	GET_CAPABILITIES {
		@Override
		public String toString() {
			return "GetCapabilities";
		}
	},

	/**
	 * WFS interface
	 */
	DESCRIBE_FEATURE_TYPE {
		@Override
		public String toString() {
			return "DescribeFeatureType";
		}
	},
	GET_FEATURE {
		@Override
		public String toString() {
			return "GetFeature";
		}
	}
}
