package com.pheonix.pojo;

import java.util.Map;



public class JwtRequest {

	private String issuer;
    private String subject;
    private Map<String, Object> claims;    
    private int jwtExpiryInDays;
    
    public JwtRequest() {}     
    
	public JwtRequest(String issuer, String subject, Map<String, Object> claims, int jwtExpiryInDays) {
		super();
		this.issuer = issuer;
		this.subject = subject;
		this.claims = claims;
		this.jwtExpiryInDays = jwtExpiryInDays;
	}


	public int getJwtExpiryInDays() {
		return jwtExpiryInDays;
	}
	public void setJwtExpiryInDays(int jwtExpiryInDays) {
		this.jwtExpiryInDays = jwtExpiryInDays;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Map<String, Object> getClaims() {
		return claims;
	}
	public void setClaims(Map<String, Object> claims) {
		this.claims = claims;
	}
    
    

   
}
