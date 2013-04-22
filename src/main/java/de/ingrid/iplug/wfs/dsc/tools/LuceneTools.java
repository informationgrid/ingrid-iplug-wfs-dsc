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
