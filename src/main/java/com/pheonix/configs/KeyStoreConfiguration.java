package com.pheonix.configs;


import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Named
@Singleton
public class KeyStoreConfiguration {
	
	@ConfigProperty(name = "jwt.keystore.location")
	String location;
	
	@ConfigProperty(name = "jwt.keystore.pwd")
	String pwd;
	
	@ConfigProperty(name = "jwt.keystore.alias")
	String alias;
	
	@ConfigProperty(name = "jwt.keystore.enable")
	boolean enable;
	
	@ConfigProperty(name = "jwt.keystore.encrypted")
	boolean encrypted;
	
	@ConfigProperty(name = "jwt.keystore.aliasPwd", defaultValue="")
	String aliasPwd;
	
	
	public String getAliasPwd() {
		return aliasPwd;
	}
	public void setAliasPwd(String aliasPwd) {
		this.aliasPwd = aliasPwd;
	}
	public boolean isEncrypted() {
		return encrypted;
	}
	public void setEncrypted(boolean encrypted) {
		this.encrypted = encrypted;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public boolean isEnable() {
		return enable;
	}
	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	@Override
	public String toString() {
		return "KeyStoreConfiguration [location=" + location + ", pwd=" + pwd + ", alias=" + alias + ", enable="
				+ enable + ", encrypted=" + encrypted + "]";
	}
	
	
	
}

