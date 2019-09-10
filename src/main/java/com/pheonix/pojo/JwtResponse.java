package com.pheonix.pojo;

import org.jose4j.jwt.JwtClaims;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class JwtResponse {

	private boolean valid;
	private JwtClaims jwtClaims;
	
	
	public JwtResponse(boolean valid, JwtClaims jwtClaims) {
		super();
		this.valid = valid;
		this.jwtClaims = jwtClaims;
	}
	
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public JwtClaims getJwtClaims() {
		return jwtClaims;
	}
	public void setJwtClaims(JwtClaims jwtClaims) {
		this.jwtClaims = jwtClaims;
	}
	
	

}
