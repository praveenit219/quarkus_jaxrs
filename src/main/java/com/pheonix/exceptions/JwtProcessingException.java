package com.pheonix.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class JwtProcessingException extends Exception implements ExceptionMapper<JwtProcessingException> {
	

	private static final long serialVersionUID = 3454947627279642140L;
	

	public JwtProcessingException() {}
 

	public JwtProcessingException(String msg) {
		super(msg);
	}
	
	
	public JwtProcessingException(String msg, Exception e) {
		super(msg,e);
	}


	@Override
	public Response toResponse(JwtProcessingException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 403, null);
	}

}
