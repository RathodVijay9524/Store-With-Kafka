package in.vijay.order_service.exceptions;

// Custom exception classes (if not already present)
public class ServiceException extends RuntimeException {
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
