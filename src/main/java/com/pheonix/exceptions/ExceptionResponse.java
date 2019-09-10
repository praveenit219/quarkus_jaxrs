package com.pheonix.exceptions;

import java.util.List;



public class ExceptionResponse {

	private String statusRef;
	private int statusCode;
	private String message;
	private String details;
	
	private List<MoreInfo> moreInfo;

	

	public String getStatusRef() {
		return statusRef;
	}

	public void setStatusRef(String statusRef) {
		this.statusRef = statusRef;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<MoreInfo> getMoreInfo() {
		return moreInfo;
	}

	public void setMoreInfo(List<MoreInfo> moreInfo) {
		this.moreInfo = moreInfo;
	}

	
	
	
}
