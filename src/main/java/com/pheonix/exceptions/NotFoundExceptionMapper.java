package com.pheonix.exceptions;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;


@Provider
public class NotFoundExceptionMapper  implements ExceptionMapper<NotFoundException> {
	
	

	public NotFoundExceptionMapper() {}


	@Override
	public Response toResponse(NotFoundException exception) {
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 404, null);
	}



}
