package com.nhom7.ecommercebackend.exception;

import com.nhom7.ecommercebackend.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
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

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleJwtValidationException(HttpServletRequest request, AuthenticationException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleDataNotFoundException(HttpServletRequest request, Exception e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
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
        error.setStatus(HttpStatus.FORBIDDEN.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(InvalidBearerTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidBearerTokenException(HttpServletRequest request, InvalidBearerTokenException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
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
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleTokenException(HttpServletRequest request, TokenException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
    @ExceptionHandler(PermissionDenyException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handlePermissionDenyException(HttpServletRequest request, PermissionDenyException e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
}
