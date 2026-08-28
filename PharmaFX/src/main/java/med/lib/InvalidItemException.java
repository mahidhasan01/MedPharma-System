package med.lib;

public class InvalidItemException extends Exception {

    public InvalidItemException(String itemId) {
        super("Item with ID: " + itemId + " is not available.");
    }

    public InvalidItemException(String itemName, String manufacturer) {
        super(itemName + " of " + manufacturer + " is not available.");
    }
}
