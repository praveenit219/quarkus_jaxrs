package com.pheonix.pojo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class JwtVerificationRequest {
	
	@NotNull @NotEmpty
	private String token;
	
	public JwtVerificationRequest() {}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}
