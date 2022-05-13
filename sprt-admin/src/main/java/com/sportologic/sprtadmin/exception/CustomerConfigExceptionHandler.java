package com.sportologic.sprtadmin.exception;

import com.sportologic.sprtadmin.dto.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler.
 *
 */
@ControllerAdvice
public class CustomerConfigExceptionHandler {

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<?> exception(Exception exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(value = SprtValidException.class)
	public ResponseEntity<?> exception(SprtValidException exception) {
		return new ResponseEntity<>(exception.getMessage(), HttpStatus.FORBIDDEN);
	}
}
