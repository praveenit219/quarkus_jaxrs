package com.pheonix.configs;

import javax.inject.Named;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Named
@Singleton
public class AuthHeaderConfigs {


	@ConfigProperty(name = "jwt.auth.clockSkew")
	long clockSkew;
	
	@ConfigProperty(name = "jwt.auth.authPattern")
	String authPattern;
	
	@ConfigProperty(name = "jwt.auth.clockSkewDifference")
	boolean clockSkewDifference;
	
	
	public boolean isClockSkewDifference() {
		return clockSkewDifference;
	}
	public void setClockSkewDifference(boolean clockSkewDifference) {
		this.clockSkewDifference = clockSkewDifference;
	}
	public long getClockSkew() {
		return clockSkew;
	}
	public void setClockSkew(long clockSkew) {
		this.clockSkew = clockSkew;
	}
	public String getAuthPattern() {
		return authPattern;
	}
	public void setAuthPattern(String authPattern) {
		this.authPattern = authPattern;
	}
	
	
	


}
