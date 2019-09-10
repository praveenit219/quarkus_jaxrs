package com.pheonix.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class JwtTokenInvalidException extends RuntimeException implements ExceptionMapper<JwtTokenInvalidException> {


	private static final long serialVersionUID = -4335896511660306030L;
	public JwtTokenInvalidException() {}

	public JwtTokenInvalidException(String msg) {
		super(msg);		
	}
	
	public JwtTokenInvalidException(String msg, Exception e) {
		super(msg,e);		
	}

	@Override
	public Response toResponse(JwtTokenInvalidException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 403, null);
	}
}
