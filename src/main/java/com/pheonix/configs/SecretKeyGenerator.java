package com.pheonix.configs;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pheonix.utils.StringUtils;


@Named
@Singleton
public class SecretKeyGenerator {

	private static final Logger log = LoggerFactory.getLogger(SecretKeyGenerator.class);


	private String sensitiveDataChecksum = null;

	
	public SecretKeyGenerator() {}

	void loadCheckSum() {
		log.info("keyinitializer .............");
		try {
			generateCheckSum("SHA-256");		
			log.info("generated check sum for SHA-256");
		} catch (NoSuchAlgorithmException | IOException e) {
			log.error("loadCheckSum - KeyIntializer, error generating checkSum at startup {}", e);

		}	
		if(!StringUtils.isEmpty(getSensitiveDataChecksum())) {
			log.info("loadCheckSum - KeyIntializer, loaded senisitive data CheckSum at startup");
		}
	}


	public String getSensitiveDataChecksum() {
		return sensitiveDataChecksum;
	}


	public void setSensitiveDataChecksum(String sensitiveDataChecksum) {
		this.sensitiveDataChecksum = sensitiveDataChecksum;
	}

	protected InputStream readProtectedResorce(){
		return getClass()
				.getClassLoader().getResourceAsStream("License.md");
	}

	private void generateCheckSum(String algorithm) throws IOException, NoSuchAlgorithmException  {
		
		MessageDigest algDigest = MessageDigest.getInstance(algorithm);
		setSensitiveDataChecksum(getFileChecksum(algDigest, readProtectedResorce()));
	}

	private static String getFileChecksum(MessageDigest digest, InputStream inputStream) {
		InputStream fis = null;
		fis = inputStream;
		byte[] byteArray = new byte[1024];
		int bytesCount = 0;
		try {
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
		} catch (IOException e) {
			log.error("io exception during file reading for message digest", e);
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				log.error("io exception during filestream close", e);
			}
		}

		byte[] bytes = digest.digest();
		StringBuilder sb = new StringBuilder();
		for(int i=0; i< bytes.length ;i++)
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		if(log.isDebugEnabled())
			log.debug("generated checksum is {} using algorithm {}", sb, digest.getAlgorithm());
		return sb.toString();
	}
}
