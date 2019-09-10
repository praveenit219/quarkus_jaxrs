package com.pheonix.pojo;

import javax.validation.constraints.NotEmpty;

public class SensitiveDataRequest {
	
	@NotEmpty
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	

}
