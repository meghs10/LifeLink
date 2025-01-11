package com.example.LikeLink.Exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingException extends RuntimeException {
    
    private String errorCode;
    private HttpStatus status;

    public BookingException(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BookingException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.status = HttpStatus.BAD_REQUEST;
    }

    public BookingException(String message, String errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
