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
 * Check if the given value is not equal to null, undefined of empty string
 * 
 * @param val
 * @returns {Boolean}
 */
function hasValue(val) {
	if (typeof val == "undefined") {
		return false; 
	} else if (val == null) {
		return false; 
	} else if (typeof val == "string" && val == "") {
		return false;
	} else {
	  return true;
	}
}

/**
 * Check if the given string is an url
 * @param str
 * @returns {Boolean}
 */
function isUrl(str) {
    var pattern = /\b(?:https?|ftp):\/\/[a-z0-9-+&@#\/%?=~_|!:,.;]*[a-z0-9-+&@#\/%=~_|]/gim;
    return str.match(pattern);
}

/**
 * Check if the given string is an email address
 * @param str
 * @returns {Boolean}
 */
function isEmail(str) {
    var pattern = /(([a-zA-Z0-9_\-\.]+)@[a-zA-Z_\-]+?(?:\.[a-zA-Z]{2,6})+)+/gim;
    return str.match(pattern);
}

/**
 * Execute the given function with the given parameters
 * @param f
 * @param args
 * @returns
 */
function call_f(f, args) {
  f.call_self = function(ars) {
	  var callstr = "";
	  if (hasValue(ars)) {
		  for(var i = 0; i < ars.length; i++) {
			  callstr += "ars["+i+"]";
			  if(i < ars.length - 1) {
				  callstr += ',';
			  }
		  }
	  }
	  return eval("this("+callstr+")");
  };
  return f.call_self(args);
}

/**
 * Replace the parts matching pattern with replacement in subject
 * @param pattern
 * @param replacement
 * @param subject
 * @returns
 */
function regReplace(pattern, replacement, subject) {
	// since the input type is a java string, we
	// need to convert it to a js string to use
	// the correct replace function
	var input = new String(subject);
	return input.replace(pattern, replacement);
}