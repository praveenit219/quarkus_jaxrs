
package com.pheonix.utils;


import javax.inject.Named;
import javax.inject.Singleton;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Named
@Singleton
public class CredentialsEncryption {

	private static final Logger log = LoggerFactory.getLogger(CredentialsEncryption.class);

	
	public String encrypt(String uniqueKey, String rawText)  {
		if(log.isDebugEnabled())
			log.debug("generating encryption for the raw text");		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPasswordCharArray(uniqueKey.toCharArray());
		return textEncryptor.encrypt(rawText);
	}
	
	
}
