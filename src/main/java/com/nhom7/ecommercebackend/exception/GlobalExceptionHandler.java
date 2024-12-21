package com.nhom7.ecommercebackend.exception;

import com.nhom7.ecommercebackend.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleJwtValidationException(HttpServletRequest request, AuthenticationException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(UnsupportedLoginException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUnsupportedLoginException(HttpServletRequest request, UnsupportedLoginException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleDataNotFoundException(HttpServletRequest request, Exception e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(NOT_FOUND.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleAccessDenied(HttpServletRequest request, org.springframework.security.access.AccessDeniedException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(FORBIDDEN.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidBearerTokenException(HttpServletRequest request, InvalidBearerTokenException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handle(HttpServletRequest request, Exception e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(INTERNAL_SERVER_ERROR.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(TokenException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleTokenException(HttpServletRequest request, TokenException e) {

        ErrorResponse error = new ErrorResponse();
        error.setTimeStamp(new Date());
        error.setStatus(UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());
        return error;
    }
    @ExceptionHandler(PermissionDenyException.class)
    @ResponseStatus(UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handlePermissionDenyException(HttpServletRequest request, PermissionDenyException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(PasswordCreationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleAppException(HttpServletRequest request, PasswordCreationException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleDataIntegrityViolationException(HttpServletRequest request, DataIntegrityViolationException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());
        return error;
    }
    @ExceptionHandler(UnsupportedPaymentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUnsupportedPaymentException(HttpServletRequest request, UnsupportedPaymentException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(UnsupportedShipmentException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleUnsupportedShipmentException(HttpServletRequest request, UnsupportedShipmentException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentException(HttpServletRequest request, ConstraintViolationException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        e.getConstraintViolations().forEach(constraintViolation -> {
            errors.add(constraintViolation.getMessage());
        });
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
}
