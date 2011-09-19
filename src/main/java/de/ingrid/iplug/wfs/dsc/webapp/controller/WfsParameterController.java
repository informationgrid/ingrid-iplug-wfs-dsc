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
import de.ingrid.iplug.wfs.dsc.webapp.object.WfsConfiguration;
import de.ingrid.iplug.wfs.dsc.webapp.validation.WfsParameterValidator;
import de.ingrid.utils.PlugDescription;

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

		pdCommandObject.put("serviceUrl", commandObject.getServiceUrl());

		pdCommandObject.setRankinTypes(true, false, false);

		// add necessary fields so iBus actually will query us
		// remove field first to prevent multiple equal entries
		pdCommandObject.removeFromList(PlugDescription.FIELDS, "incl_meta");
		pdCommandObject.addField("incl_meta");
		pdCommandObject.removeFromList(PlugDescription.FIELDS, "t01_object.obj_class");
		pdCommandObject.addField("t01_object.obj_class");
		pdCommandObject.removeFromList(PlugDescription.FIELDS, "metaclass");
		pdCommandObject.addField("metaclass");

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
