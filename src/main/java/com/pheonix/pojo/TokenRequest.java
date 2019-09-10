package com.pheonix.pojo;

import java.util.List;



public class TokenRequest {

	
	
	private String id;
	
	
	private int expiration;
	
	private String eServiceId;
	
	private List<Services> services;

	private String user;
	
	public TokenRequest() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public TokenRequest( String id, int expriation,  String eServiceId,  List<Services> services, String user) {
		super();
		this.eServiceId = id;
		this.expiration = expriation;
		this.eServiceId = eServiceId;
		this.services = services;
		this.user = user;
	}


	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
	



	public int getExpiration() {
		return expiration;
	}

	public void setExpiration(int expiration) {
		this.expiration = expiration;
	}

	public String geteServiceId() {
		return eServiceId;
	}



	public void seteServiceId(String eServiceId) {
		this.eServiceId = eServiceId;
	}



	public List<Services> getServices() {
		return services;
	}



	public void setServices(List<Services> services) {
		this.services = services;
	}

	@Override
	public String toString() {
		return "TokenRequest [id=" + id + ", expiration=" + expiration + ", eServiceId=" + eServiceId + ", services="
				+ services + ", user=" + user + "]";
	}

	
}
