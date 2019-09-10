package com.pheonix.exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.pheonix.utils.ExceptionUtils;

@Provider
public class ConstraintViolationExceptionsMapper implements ExceptionMapper<ConstraintViolationException> {

	
	@Override
	public Response toResponse(ConstraintViolationException exception) {
		Set<ConstraintViolation<?>> constraints = exception.getConstraintViolations();
		MoreInfo moreInfoc = null;
		List<MoreInfo> moreInfo = null;
		if(!constraints.isEmpty()) {
			moreInfo = new ArrayList<>();
			for( ConstraintViolation cv : constraints ) {
				moreInfoc = new MoreInfo();				
				moreInfoc.setName(cv.getPropertyPath().toString());
				moreInfoc.setReason(cv.getMessage());
				moreInfo.add(moreInfoc);
			}
		}		
		return ExceptionUtils.buildExceptionResponse(exception.getLocalizedMessage(), 400, moreInfo);
	}

}
