package com.pheonix.pojo;

import java.util.Map;



public class JwtClaimsResponse {

	private boolean valid;
	private Map claims;
	private String issuer;
	
	public JwtClaimsResponse() {}
	
	public JwtClaimsResponse(boolean valid, Map claims, String issuer) {
		super();
		this.valid = valid;
		this.claims = claims;
		this.issuer = issuer;
	}
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public Map getClaims() {
		return claims;
	}

	public void setClaims(Map claims) {
		this.claims = claims;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	
	
	

}
