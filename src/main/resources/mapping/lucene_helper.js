importPackage(Packages.org.apache.lucene.document);

function addToDoc(document, field, content, tokenized) {
	if (typeof content != "undefined" && content != null) {
		if (log.isDebugEnabled()) {
			log.debug("Add '" + field + "'='" + content + "' to lucene index");
		}
		var analyzed = Field.Index.ANALYZED;
		if (!tokenized) analyzed = Field.Index.NOT_ANALYZED;
		document.add(new Field(field, content, Field.Store.YES, analyzed));
		document.add(new Field("content", content, Field.Store.NO, analyzed));
		document.add(new Field("content", LuceneTools.filterTerm(content), Field.Store.NO, Field.Index.ANALYZED));
	}
}

function addNumericToDoc(document, field, content) {
	if (typeof content != "undefined" && content != null) {
        try {
    		if (log.isDebugEnabled()) {
    			log.debug("Add numeric '" + field + "'='" + content + "' to lucene index.");
    		}
            document.add(new NumericField(field, Field.Store.YES, true).setDoubleValue(content));
        } catch (e) {
            if (log.isDebugEnabled()) {
                log.debug("Value '" + content + "' is not a number. Ignoring field '" + field + "'.");
            }
        }
	}
}
