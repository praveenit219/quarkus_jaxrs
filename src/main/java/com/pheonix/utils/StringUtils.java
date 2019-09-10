package com.pheonix.utils;



public abstract class StringUtils {
	
	public static boolean isEmpty(Object str) {
		return (str == null || "".equals(str));
	}

	public static String findPathForJWtorJWE(String uri) {
		if(!isEmpty(uri)) {
			if(uri.contains("jwt")) {
				return "jwt";
			}
			if(uri.contains("jwe") ) {
				return "jwe";
			}
		} 

		return null;
	}
}
