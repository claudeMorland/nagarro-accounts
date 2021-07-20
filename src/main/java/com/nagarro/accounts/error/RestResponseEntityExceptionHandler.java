package com.nagarro.accounts.error;

import static com.nagarro.accounts.common.ApplicationConstants.*;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.nagarro.accounts.error.functional.AccountBadRequestException;
import com.nagarro.accounts.error.functional.AuthentificationBadRequestException;
import com.nagarro.accounts.error.functional.ResourceNotFoundException;
import com.nagarro.accounts.error.functional.UserNotFoundRequestException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

	public RestResponseEntityExceptionHandler() {
		super();
	}

	@InitBinder
	protected void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) throws IllegalArgumentException {
				setValue(LocalDate.parse(text, java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT_API)));
			}
		});
		
	}

	// API

	// 400

	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ResponseEntity<Object> handleBadRequest(final DataIntegrityViolationException ex, final WebRequest request) {
		final String bodyOfResponse = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		logger.error("DataBase integrity violation", ex);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		final String bodyOfResponse = "HTTP Message not readable";
		logger.error(bodyOfResponse, ex);
		// ex.getCause() instanceof JsonMappingException, JsonParseException // for
		// additional information later on
		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
			final HttpHeaders headers, final HttpStatus status, final WebRequest request) {
		final String bodyOfResponse = "Invalid argument";
		logger.error(bodyOfResponse, ex);
		return handleExceptionInternal(ex, bodyOfResponse, headers, HttpStatus.BAD_REQUEST, request);
	}

	// 409

	@ExceptionHandler({ InvalidDataAccessApiUsageException.class, DataAccessException.class })
	protected ResponseEntity<Object> handleConflict(final RuntimeException ex, final WebRequest request) {
		final String bodyOfResponse = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		logger.error("Invalid data access", ex);
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}

	// 412

	// 500

	@ExceptionHandler({ NullPointerException.class, IllegalArgumentException.class, IllegalStateException.class })
	/* 500 */public ResponseEntity<Object> handleInternal(final RuntimeException ex, final WebRequest request) {
		logger.error("500 Status Code", ex);
		final String bodyOfResponse = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	// 401
	@ExceptionHandler({ AccessDeniedException.class })
	public ResponseEntity<Object> handleAccessDenied(final AccessDeniedException ex, final WebRequest request) {
		logger.error("Access denied exception", ex);
		final String bodyOfResponse = "Unauthorized access";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@Override
	public ResponseEntity<Object> handleMissingPathVariable(
			MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		final String bodyOfResponse = "Missing path variable";
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<Object> handleJwtException(JwtException ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		final String bodyOfResponse = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwtException(final ExpiredJwtException ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		final String bodyOfResponse = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}

	/**
	 * Exception thrown when
	 * {@link org.springframework.validation.annotation.Validated} is used in
	 * controller.
	 * 
	 * @param ex
	 * @param request
	 * @return
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(final ConstraintViolationException ex,
			final WebRequest request) {
		//
		final List<String> errors = new ArrayList<>();
		for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			errors.add(violation.getMessage());
		}
		logger.error(ex.getLocalizedMessage(), ex);
		return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	// 404

    @ExceptionHandler(value = { ResourceNotFoundException.class})
    protected ResponseEntity<Object> handleResourceNotFoundException(final ResourceNotFoundException ex, final WebRequest request) {
    	logger.error(ex.getLocalizedMessage(), ex);
    	final String bodyOfResponse = ex.getMessage();
        return handleExceptionInternal(ex, bodyOfResponse, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }
    

	
	@ExceptionHandler(value = { AuthentificationBadRequestException.class, AccountBadRequestException.class, DateTimeParseException.class})
	public ResponseEntity<Object> handleFunctionalBadRequestException(final RuntimeException ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		String message = ex.getMessage();
		return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	@ExceptionHandler(value = { UserNotFoundRequestException.class})
	public ResponseEntity<Object> handleUserNotFoundRequestException(final UserNotFoundRequestException ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		String message = ex.getMessage();
		return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	
	
	@ExceptionHandler(value = { MethodArgumentTypeMismatchException.class})
	public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		String message = ex.getMessage();
		if (ex.getCause() != null) {
			message = "Invalid argument type : " + ex.getCause().getMessage();
		} 
		return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handle(final Exception ex, final WebRequest request) {
		logger.error(ex.getLocalizedMessage(), ex);
		String message = UNKNOWN_ERROR_PLEASE_CONTACT_THE_ADMINISTATROR;
		if (ex instanceof MethodArgumentTypeMismatchException) {
			message = "parameter passed in parameter parsing malformed or abnormal!";
		}
		return handleExceptionInternal(ex, message, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

}