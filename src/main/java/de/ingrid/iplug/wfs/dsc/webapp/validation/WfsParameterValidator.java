/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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
package de.ingrid.iplug.wfs.dsc.webapp.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import de.ingrid.admin.validation.AbstractValidator;
import de.ingrid.admin.validation.IErrorKeys;
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
		this.rejectIfNotNumeric(errors, "featurePreviewLimit");
		return errors;
	}

	protected void rejectIfNotNumeric(final BindingResult errors, String field) {
		String value = getString(errors, field);
		if (!StringUtils.isNumeric(value)) {
			rejectError(errors, field, IErrorKeys.INVALID);
		}
	}
}
