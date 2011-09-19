package de.ingrid.iplug.wfs.dsc.webapp.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.iplug.wfs.dsc.webapp.object.WfsConfiguration;

/**
 * Validator for wfs parameter dialog.
 * 
 * 
 * @author joachim@wemove.com
 *
 */
@Service
public class WfsParameterValidator extends
AbstractValidator<WfsConfiguration> {

	public final Errors validateWfsParams(final BindingResult errors) {
		this.rejectIfEmptyOrWhitespace(errors, "serviceUrl");
		return errors;
	}
}
