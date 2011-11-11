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
 * Execute the given function with the given parameters
 * @param f
 * @param args
 * @returns
 */
function call_f(f,args) {
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
