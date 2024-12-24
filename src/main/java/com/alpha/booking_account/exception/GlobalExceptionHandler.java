package com.alpha.booking_account.exception;


import com.alpha.booking_account.model.response.CustomResponseModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String ERROR_KEY = "error";
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CustomResponseModel<Map<String, String>>> handleMissingParameterException(HttpMessageNotReadableException ex) {
        logger.error("Bad Request: ", ex);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, ex.getMessage());

        CustomResponseModel<Map<String, String>> response = new CustomResponseModel<>();
        response.setResult_code(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Bad Request");
        response.setError(errorMap);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CustomResponseModel<Map<String, String>>> handleValidationException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });

        CustomResponseModel<Map<String, String>> response = new CustomResponseModel<>();
        response.setResult_code(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");
        response.setError(errors);
        logger.error("Bad Request: {}", response);
        return ResponseEntity.badRequest().body(response);
    }
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<CustomResponseModel<Map<String, String>>> handleMultipartException(MultipartException ex) {
        logger.error("Bad Request: ", ex);

        String errorMessage = "Failed to process Excel file: ";
        if (ex.getCause() instanceof IllegalStateException && ex.getMessage().contains("File upload failed because the supplied InputStream is null")) {
            errorMessage += "The supplied file was empty (zero bytes long)";
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(ERROR_KEY, errorMessage);

            CustomResponseModel<Map<String, String>> response = new CustomResponseModel<>();
            response.setResult_code(HttpStatus.BAD_REQUEST.value());
            response.setMessage("Bad Request");
            response.setError(errorMap);
            logger.error("Bad Request: {}", response);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else {
            errorMessage += ex.getMessage();
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put(ERROR_KEY, errorMessage);

            CustomResponseModel<Map<String, String>> response = new CustomResponseModel<>();
            response.setResult_code(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Internal Server Error ==");
            response.setError(errorMap);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponseModel<Map<String, String>>> handleInternalServerError(Exception ex) {
        logger.error("Internal Server Error:==> ", ex);
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(ERROR_KEY, ex.getMessage());

        CustomResponseModel<Map<String, String>> response = new CustomResponseModel<>();
        response.setResult_code(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setMessage("Internal Server Error");
        response.setError(errorMap);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
