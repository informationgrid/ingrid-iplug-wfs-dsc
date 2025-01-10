/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.iplug.wfs.dsc;

import org.springframework.stereotype.Service;

import de.ingrid.iplug.IPlugdescriptionFieldFilter;

/**
 * This class is used to filter the values that shall be inside the
 * PlugDescritption when sending it to the iBus. It is not necessary to
 * send the whole, and sometimes huge object over the network.
 * @author André Wallat
 *
 */
@Service
public class MappingFilter implements IPlugdescriptionFieldFilter {

	@Override
	public boolean filter(Object object) {
		//String key = object.toString();
		/*
		if ("rankingMul".equals(key) ||
			"rankingAdd".equals(key) ||
			"mapping".equals(key)) {
			return true;
		}
		 */
		return false;
	}
}
