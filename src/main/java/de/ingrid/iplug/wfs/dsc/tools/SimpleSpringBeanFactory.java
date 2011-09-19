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
