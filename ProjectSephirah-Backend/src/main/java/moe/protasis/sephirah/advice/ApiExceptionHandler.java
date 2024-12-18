package moe.protasis.sephirah.advice;

import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import moe.protasis.sephirah.exception.APIException;
import moe.protasis.sephirah.exception.RateLimitedException;
import moe.protasis.sephirah.response.StandardAPIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> OnException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(new StandardAPIResponse(2,"server error"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> OnBadRequestException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return ResponseEntity.status(400).body(new StandardAPIResponse(7, "invalid request"));
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<?> OnServletException(ServletException e) {
        return ResponseEntity.status(400).body(new StandardAPIResponse(8, "invalid request"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> OnNoHandlerFoundException() {
        return ResponseEntity.status(404).body(new StandardAPIResponse(15, "not found"));
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> OnStandardException(APIException ex) {
        return ResponseEntity.status(ex.getCode()).body(new StandardAPIResponse(ex.getStatusCode(), ex.getMessage(), ex.getErrorData()));
    }

    @ExceptionHandler(RateLimitedException.class)
    public ResponseEntity<?> OnRateLimited(RateLimitedException ex) {
        return ResponseEntity.status(ex.getCode()).body(new StandardAPIResponse(ex.getStatusCode(), ex.getMessage())
                .data("retryAfter", ex.getRetryAfter().getMillis()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> OnMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.warn(ex.getMessage());
        return ResponseEntity.status(400).body(new StandardAPIResponse(15, "invalid request"));
    }
}
