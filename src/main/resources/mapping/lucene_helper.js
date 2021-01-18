/*
 * **************************************************-
 * ingrid-iplug-wfs-dsc:war
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
if (javaVersion.indexOf( "1.8" ) === 0) {
    load("nashorn:mozilla_compat.js");
}

//importPackage(Packages.org.apache.lucene.document);

function addToDoc(document, field, content, tokenized) {
	if (typeof content != "undefined" && content != null) {
		if (log.isDebugEnabled()) {
			log.debug("Add '" + field + "'='" + content + "' to lucene index");
		}
		document.put( field, content );
		document.put( "content", content );
	}
}

function addNumericToDoc(document, field, content) {
	if (typeof content != "undefined" && content != null) {
        try {
    		if (log.isDebugEnabled()) {
    			log.debug("Add numeric '" + field + "'='" + content + "' to lucene index.");
    		}
            document.put( field, setDoubleValue(content) );
        } catch (e) {
            if (log.isDebugEnabled()) {
                log.debug("Value '" + content + "' is not a number. Ignoring field '" + field + "'.");
            }
        }
	}
}
