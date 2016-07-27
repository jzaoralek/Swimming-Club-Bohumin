package com.jzaoralek.scb.dataservice.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpStatusCodeException;

import com.jzaoralek.scb.dataservice.exception.ApiExceptionJson;
import com.jzaoralek.scb.dataservice.exception.ApiExceptionJson.ApiExceptionJsonType;
import com.jzaoralek.scb.dataservice.exception.ScbValidationException;

public abstract class AbstractScbDataServiceController {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractScbDataServiceController.class);

	@Autowired
    private HttpServletResponse response;

	/**
     * Pokud metoda kontroleru vyhodí výjimku, kontrolu převezme tato metoda
     *
     * Aplikuje se na všechny kontrolery dědící z této třídy
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiExceptionJson handleException(Exception exception) {
    	LOG.error("Exception caught in controller", exception);
    	ApiExceptionJson ret = new ApiExceptionJson();
    	ret.setType(ApiExceptionJsonType.RUNTIME);
    	if (exception instanceof HttpStatusCodeException) {
    		HttpStatusCodeException ex = (HttpStatusCodeException)exception;
    		ret.setCode(ex.getStatusCode().toString());
    		ret.setName(ex.getStatusText());
    		ret.setDescription(ex.getMessage());
    		response.setStatus(Integer.valueOf(ex.getStatusCode().toString()));
    	} else {
    		ret.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
    		ret.setName(HttpStatus.INTERNAL_SERVER_ERROR.name());
    		ret.setDescription(exception.getMessage());
    		response.setStatus(Integer.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.toString()));
    	}
    	return ret;
    }

    @ExceptionHandler(ScbValidationException.class)
    @ResponseBody
    public ApiExceptionJson handleException(ScbValidationException exception) {
    	LOG.error("Exception caught in controller", exception);
    	ApiExceptionJson ret = new ApiExceptionJson();
    	ret.setType(ApiExceptionJsonType.VALIDATION);
    	ret.setDescription(exception.getMessage());

    	return ret;
    }
}
