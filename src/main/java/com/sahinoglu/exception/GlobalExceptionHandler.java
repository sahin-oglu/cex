package com.sahinoglu.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private ResponseEntity<ExceptionResponse> buildResponse(Exception ex, HttpServletRequest request,
			HttpStatus status) {
		ExceptionResponse response = new ExceptionResponse(LocalDateTime.now(), status.value(),
				status.getReasonPhrase(), ex.getMessage(), request.getRequestURI());

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {

		return buildResponse(ex, request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {

		return buildResponse(ex, request, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ExceptionResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {

		return buildResponse(ex, request, HttpStatus.FORBIDDEN);
	}

	// fallback
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGeneric(Exception ex, HttpServletRequest request) {

		return buildResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}