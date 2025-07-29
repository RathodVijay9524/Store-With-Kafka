package in.vijay.order_service.exceptions;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
    public OrderProcessingException(String message) {
        super(message);
    }
}

