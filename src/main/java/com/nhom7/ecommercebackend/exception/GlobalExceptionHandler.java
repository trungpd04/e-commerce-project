package com.nhom7.ecommercebackend.exception;

import com.nhom7.ecommercebackend.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handle(HttpServletRequest request, Exception e) {
        ErrorResponse error = new ErrorResponse();

        error.setTimeStamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        error.setError(errors);
        error.setPath(request.getServletPath());

        return error;
    }
}
