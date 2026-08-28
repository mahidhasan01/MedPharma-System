package med.lib;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

public class Order implements Serializable {

    private String orderId, itemId, orderBy, status;
    private int quantity;
    private LocalDate orderDate, deliveryDate;
    private ArrayList<Item> items;

    public Order(String itemId, String orderBy, int quantity) {
        this.itemId = itemId;
        this.orderBy = orderBy;
        this.quantity = quantity;
        this.items = new ArrayList<>();
        setOrderId();
        this.status = "New";
        this.orderDate = LocalDate.now();
    }

    protected void setOrderId() {
        Random rand = new Random();
        this.orderId = "O-" + String.format("%04d", rand.nextInt(10000));
    }

    public String getOrderId() {
        return orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDate deliveryDate) {
        this.deliveryDate = deliveryDate;
    }


    @Override
    public String toString() {
        return "Order Details:\n" +
                "Order ID: " + orderId + "\n" +
                "Item ID: " + itemId + "\n" +
                "Ordered By: " + orderBy + "\n" +
                "Quantity: " + quantity + "\n" +
                "Status: " + status + "\n" +
                "Order Date: " + orderDate + "\n" +
                "Delivery Date: " + deliveryDate;
    }


    public void completeOrder() {
        this.status = "Complete";
        this.deliveryDate = LocalDate.now();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public double getTotalBill() {
        double total = 0;
        for (int i = 0; i < items.size(); i++) {
            total += items.get(i).getBill();
        }
        return total;
    }
}
