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

function addToDoc(document, field, content, tokenized) {
	if (typeof content != "undefined" && content != null) {
		log.debug("Add '" + field + "'='" + content + "' to lucene index");
		document.put( field, content );
		document.put( "content", content );
	}
}

function addNumericToDoc(document, field, content) {
	if (typeof content != "undefined" && content != null) {
        try {
			log.debug("Add numeric '" + field + "'='" + content + "' to lucene index.");
            document.put( field, content );
        } catch (e) {
			log.debug("Value '" + content + "' is not a number. Ignoring field '" + field + "'.");
        }
	}
}
