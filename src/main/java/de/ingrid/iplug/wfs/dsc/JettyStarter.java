/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * This class starts a Jetty server where the webapp will be executed.
 * @author André Wallat
 *
 */
public class JettyStarter {
	private static final Log log = LogFactory.getLog(JettyStarter.class);

	private static String DEFAULT_WEBAPP_DIR    = "webapp";

	private static int    DEFAULT_JETTY_PORT    = 8082;


	public static void main(String[] args) throws Exception {
		if (!System.getProperties().containsKey("jetty.webapp"))
			log.warn("Property 'jetty.webapp' not defined! Using default webapp directory, which is '"+DEFAULT_WEBAPP_DIR+"'.");
		if (!System.getProperties().containsKey("jetty.port"))
			log.warn("Property 'jetty.port' not defined! Using default port, which is '"+DEFAULT_JETTY_PORT+"'.");

		init();
	}

	private static void init() throws Exception {
		WebAppContext webAppContext = new WebAppContext(System.getProperty("jetty.webapp", DEFAULT_WEBAPP_DIR), "/");

		Server server = new Server(Integer.getInteger("jetty.port", DEFAULT_JETTY_PORT));
		// fix slow startup time on virtual machine env.
		HashSessionIdManager hsim = new HashSessionIdManager();
		hsim.setRandom(new Random());
		server.setSessionIdManager(hsim);
		server.setHandler(webAppContext);
		server.start();
	}

}
