package in.vijay.dto.order;

public enum OrderStatus {

    /** Order created and awaiting further processing */
    CREATED,

    /** Inventory has been reserved for the order */
    RESERVED,

    /** Payment has been completed successfully */
    PAYMENT_COMPLETED,

    /** Order has been shipped to the customer */
    SHIPPED,

    /** Order is cancelled but pending confirmation from another service (e.g., inventory or shipping) */
    CANCELLED_PENDING,

    /** Order processing failed due to an error (e.g., payment or inventory) */
    FAILED,

    /** Order successfully delivered and completed */
    SUCCESS;
}
