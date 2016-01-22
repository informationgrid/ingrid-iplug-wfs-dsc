/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.admin.command.PlugdescriptionCommandObject;
import de.ingrid.admin.controller.AbstractController;
import de.ingrid.iplug.wfs.dsc.Configuration;
import de.ingrid.iplug.wfs.dsc.WfsDscSearchPlug;
import de.ingrid.iplug.wfs.dsc.webapp.object.WfsConfiguration;
import de.ingrid.iplug.wfs.dsc.webapp.validation.WfsParameterValidator;

/**
 * Control the wfs parameter page.
 * 
 * @author joachim@wemove.com
 * 
 */
@Controller
@SessionAttributes("plugDescription")
public class WfsParameterController extends AbstractController {
	private final WfsParameterValidator _validator;

	@Autowired
	public WfsParameterController(WfsParameterValidator validator) {
		this._validator = validator;
	}

	@RequestMapping(value = { "/iplug-pages/welcome.html",
	"/iplug-pages/wfsParams.html" }, method = RequestMethod.GET)
	public String getParameters(
			final ModelMap modelMap,
			@ModelAttribute("plugDescription") final PlugdescriptionCommandObject commandObject) {

		WfsConfiguration wfsConfig = new WfsConfiguration();

		this.mapConfigFromPD(wfsConfig, commandObject);

		// write object into session
		modelMap.addAttribute("wfsConfig", wfsConfig);

		return AdminViews.WFS_PARAMS;
	}

	@RequestMapping(value = "/iplug-pages/wfsParams.html", method = RequestMethod.POST)
	public String post(
			@ModelAttribute("wfsConfig") final WfsConfiguration commandObject,
			final BindingResult errors,
			@ModelAttribute("plugDescription") final PlugdescriptionCommandObject pdCommandObject) {

		// check if page contains any errors
		if (this._validator.validateWfsParams(errors).hasErrors()) {
			return AdminViews.WFS_PARAMS;
		}

		// put values into plugdescription
		this.mapParamsToPD(commandObject, pdCommandObject);

		return AdminViews.SAVE;
	}

	private void mapParamsToPD(WfsConfiguration commandObject,
			PlugdescriptionCommandObject pdCommandObject) {

	    Configuration conf = WfsDscSearchPlug.conf;
		conf.serviceUrl = commandObject.getServiceUrl();

		// add required datatypes to PD
		// -> is added in GeneralController with forced added datatype!
		//pdCommandObject.addDataType("dsc_wfs");
		//pdCommandObject.addDataType("wfs");
		//pdCommandObject.addDataType("IDF_1.0");
	}

	private void mapConfigFromPD(WfsConfiguration mapConfig,
			PlugdescriptionCommandObject commandObject) {

		if (commandObject.containsKey("serviceUrl")) {
			mapConfig.setServiceUrl(commandObject.getString("serviceUrl"));
		}
	}


}
