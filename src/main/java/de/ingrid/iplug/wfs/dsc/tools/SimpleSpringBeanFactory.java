/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.tools;

import de.ingrid.utils.tool.SpringUtil;

/**
 * @author joachim@wemove.com
 *
 */
public enum SimpleSpringBeanFactory {

	// Guaranteed to be the single instance
	INSTANCE;

	SpringUtil util = null;

	public <T> T getBean(String beanName, Class<T> clazz) {
		if (this.util == null) {
			this.util = new SpringUtil("spring/spring.xml");
		}
		return this.util.getBean(beanName, clazz);
	}

	public void setBeanConfig(String beanConfigFile) {
		this.util = new SpringUtil(beanConfigFile);
	}

}
