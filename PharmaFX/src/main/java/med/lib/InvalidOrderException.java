package med.lib;

public class InvalidOrderException extends Exception {

    public InvalidOrderException(String orderId) {
        super("Order with ID: " + orderId + " is not available.");
    }

    public InvalidOrderException(String orderId, String orderDetails) {
        super("Order with ID: " + orderId + " is not available. Details: " + orderDetails);
    }
}
