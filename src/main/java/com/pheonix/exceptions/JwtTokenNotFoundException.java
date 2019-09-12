package com.pheonix.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.pheonix.utils.ExceptionUtils;

public class JwtTokenNotFoundException extends RuntimeException implements ExceptionMapper<JwtTokenNotFoundException> {

	private static final long serialVersionUID = 2733185269519094982L;

	public JwtTokenNotFoundException() {}

	public JwtTokenNotFoundException(String msg) {
		super(msg);		
	}
	
	public JwtTokenNotFoundException(String msg, Exception e) {
		super(msg,e);		
	}

	@Override
	public Response toResponse(JwtTokenNotFoundException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 404, null);
	}

}
