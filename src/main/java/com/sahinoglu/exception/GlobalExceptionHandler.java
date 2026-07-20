package com.sahinoglu.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	private ResponseEntity<ExceptionResponse> buildResponse(String message, HttpServletRequest request,
			HttpStatus status) {
		ExceptionResponse response = new ExceptionResponse(LocalDateTime.now(), status.value(),
				status.getReasonPhrase(), message, request.getRequestURI());

		return ResponseEntity.status(status).body(response);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ExceptionResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {

		return buildResponse(ex.getMessage(), request, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {

		return buildResponse(ex.getMessage(), request, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ExceptionResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {

		return buildResponse(ex.getMessage(), request, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(AuthorizationException.class)
	public ResponseEntity<ExceptionResponse> handleAuthorization(AuthorizationException ex,
			HttpServletRequest request) {

		return buildResponse(ex.getMessage(), request, HttpStatus.UNAUTHORIZED);
	}

	// fallback
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ExceptionResponse> handleGeneric(Exception ex, HttpServletRequest request) {

		log.error("Unhandled exception on {}", request.getRequestURI(), ex);

		return buildResponse("An unexpected error occurred", request, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
