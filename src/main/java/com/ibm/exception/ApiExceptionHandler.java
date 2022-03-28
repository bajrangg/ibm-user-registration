package com.ibm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ApiExceptionHandler {

// Invalid request - Look for validation messages list... populate the field name and corresponding message
// Geo location service failure - Provide only the message from the service with internal server error.
// Geo location service response - validate country

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * Handles the invalid input provided in request. Thrown by javax validation framework
     */
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid request. See errors for more details."
        );

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(),
                    fieldError.getDefaultMessage());
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    /**
     * Handling of the exception thrown for invalid input JSON
     */
    public ResponseEntity<ErrorResponse> handleInvalidJSON(HttpMessageNotReadableException ex)
    {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(GeoLocationClientException.class)
    /**
     * Thrown when the api server is sent invalid client data(ip)
     */
    public ResponseEntity<ErrorResponse> handleValidationExceptions(GeoLocationClientException ex)
    {
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(GeoLocationServerException.class)
    /**
     * Thrown when the api server is down
     */
    public ResponseEntity<ErrorResponse> handleValidationExceptions(GeoLocationServerException ex)
    {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /**
     * Build the consistent error response for all exception scenarios.
     * @param exception - Exception thrown by the api
     * @param httpStatus - Httpstatus to be set
     * @return ErrorResponse
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, HttpStatus httpStatus)
    {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), exception.getMessage());
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

}
