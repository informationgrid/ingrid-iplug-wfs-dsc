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
/**
 * 
 */
package de.ingrid.iplug.wfs.dsc.tools;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.admin.search.Stemmer;

/**
 * @author joachim
 *
 */
@Service
public class LuceneTools {

    private static Stemmer _defaultStemmer;

	/**
	 * @param term
	 * @return filtered term
	 * @throws IOException
	 */
	public static String filterTerm(String term) throws IOException {
		String result = "";

		TokenStream ts = _defaultStemmer.getAnalyzer().tokenStream(null, new StringReader(term));
		Token token = ts.next();
		while (null != token) {
			result = result + " " + token.termText();
			token = ts.next();
		}

		return result.trim();
	}

	/** Injects default stemmer via autowiring !
     * @param defaultStemmer
     */
    @Autowired
    public void setDefaultStemmer(Stemmer defaultStemmer) {
    	_defaultStemmer = defaultStemmer;
	}
}
