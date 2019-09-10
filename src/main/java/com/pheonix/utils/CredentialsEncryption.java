
package com.pheonix.utils;


import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Named
@Singleton
public class CredentialsEncryption {

	private static final Logger log = LoggerFactory.getLogger(CredentialsEncryption.class);


	
	public static String encrypt(String uniqueKey, String rawText)  {
		
		return null; 
	}
}
