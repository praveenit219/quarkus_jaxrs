package com.pheonix.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class JwtTokenExpiredException extends Exception implements ExceptionMapper<JwtTokenExpiredException> {


	private static final long serialVersionUID = -4335896511660306030L;
	public JwtTokenExpiredException() {}

	public JwtTokenExpiredException(String msg) {
		super(msg);		
	}
	
	public JwtTokenExpiredException(String msg, Exception e) {
		super(msg,e);		
	}

	@Override
	public Response toResponse(JwtTokenExpiredException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 403, null);
	}
}
