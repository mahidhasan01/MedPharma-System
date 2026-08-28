package med.lib;

import java.io.Serializable;
import java.util.Random;

public abstract class Item implements Serializable {

    private String name, id, manufacturer;
    private int quantity;
    private double unitPrice;

    public Item(String name, String manufacturer, double unitPrice, int quantity) {
        this.name = name;
        this.manufacturer = manufacturer;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        setId();
    }

    protected void addPrefixCodeToId(String prefix) {
        if (this.id == null) {
            setId();
        }
        this.id = prefix + this.id;
    }

    public void setId() {
        Random rand = new Random();
        this.id = String.format("%04d", rand.nextInt(10000));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return this.name;
    }

    public String getManufacturer() {
        return this.manufacturer;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public double getUnitPrice() {
        return this.unitPrice;
    }

    public String getId() {
        return this.id;
    }

    public void increaseQuantity(int increasedAmt) {
        this.quantity += increasedAmt;
    }

    public boolean decreaseQuantity(int decreasedAmt) throws OutOfStockException {
        if (this.quantity >= decreasedAmt) {
            this.quantity -= decreasedAmt;
            return true;
        }
        throw new OutOfStockException("Not enough stock available for item " + this.name);
    }

    @Override
    public String toString() {
        return "Name : " + this.name + "\n" +
                "Id : " + this.id + "\n" +
                "Quantity : " + this.quantity + "\n" +
                "Manufacturer : " + this.manufacturer + "\n" +
                "Unit Price : " + this.unitPrice;
    }

    public double getBill() {
        return this.quantity * this.unitPrice;
    }
}
