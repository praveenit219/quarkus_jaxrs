
package com.pheonix.utils;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
@Singleton
public class CredentialsDecryption{

	private static final Logger log = LoggerFactory.getLogger(CredentialsDecryption.class);	

	public String decrypt(String uniqueKey, String encryptedData) {
		if(log.isDebugEnabled())
			log.debug("generating decryption from the encrypted data {}", encryptedData);		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPasswordCharArray(uniqueKey.toCharArray());
		return textEncryptor.decrypt(encryptedData);
	}
}
