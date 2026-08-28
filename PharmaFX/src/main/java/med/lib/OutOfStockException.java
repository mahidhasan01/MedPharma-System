package med.lib;

public class OutOfStockException extends Exception {

    public OutOfStockException(String msg) {
        super(msg);
    }

    public OutOfStockException(String itemId, int availableQuantity) {
        super("Only " + availableQuantity + " items available for item " + itemId + ". Order more items.");
    }
}
