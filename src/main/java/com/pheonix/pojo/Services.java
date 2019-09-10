package com.pheonix.pojo;

import javax.validation.constraints.NotBlank;

public class Services {
	
	@NotBlank
	private String gateway;
	
	@NotBlank
	private String partnerId;
	
	public Services() {} 
	
	
	public Services(String gateway, String partnerId) {
		super();
		this.gateway = gateway;
		this.partnerId = partnerId;
	}
	
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getPartnerId() {
		return partnerId;
	}
	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}
	
	@Override
	public String toString() {
		return "Services [gateway=" + gateway + ", partnerId=" + partnerId + "]";
	}	


}
