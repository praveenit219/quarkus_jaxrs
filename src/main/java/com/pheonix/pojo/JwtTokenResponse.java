package com.pheonix.pojo;

public class JwtTokenResponse {

	
    private String token;
    private String expiration;
    private String status;
    
    public JwtTokenResponse() {}
    
	public JwtTokenResponse(String token, String expiration, String status) {
		super();
		this.token = token;
		this.expiration = expiration;
		this.status = status;
	}
	
	
	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
   
    

    

}
