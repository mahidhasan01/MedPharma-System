package med.lib;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class MedPharma implements Serializable {

    private String name;
    private ArrayList<Item> items = new ArrayList<>();
    private ArrayList<Order> requestedItems = new ArrayList<>();

    public MedPharma(String name) {
        this.name = name;
    }

    public ArrayList<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    public boolean saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(this);
            return true;
        } catch (IOException e) {
            System.out.println("Could not save records: " + e.getMessage());
            return false;
        }
    }

    public static MedPharma loadFromFileOrCreate(String filePath, String defaultName) {
        File f = new File(filePath);
        if (!f.exists()) return new MedPharma(defaultName);
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof MedPharma) return (MedPharma) obj;
        } catch (Exception e) {
            System.out.println("Could not load existing records, starting fresh.");
        }
        return new MedPharma(defaultName);
    }

    public static MedPharma load(String filePath) {
        return loadFromFileOrCreate(filePath, "Pharma Store");
    }

    public Item findItem(String id) throws InvalidItemException {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getId().equals(id)) {
                return item;
            }
        }
        throw new InvalidItemException("Item with ID: " + id + " is not available");
    }

    public Order findOrder(String orderId) throws InvalidOrderException {
        for (int i = 0; i < requestedItems.size(); i++) {
            Order order = requestedItems.get(i);
            if (order.getOrderId().equals(orderId) && !order.getStatus().equalsIgnoreCase("complete")) {
                return order;
            }
        }
        throw new InvalidOrderException("Order ID: " + orderId + " not found or already completed.");
    }

    public Order findOrder(String itemId, String orderBy, LocalDate orderDate) throws InvalidOrderException {
        for (int i = 0; i < requestedItems.size(); i++) {
            Order order = requestedItems.get(i);
            if (order.getItemId().equals(itemId)
                    && order.getOrderBy().equals(orderBy)
                    && order.getOrderDate().equals(orderDate)
                    && !order.getStatus().equalsIgnoreCase("complete")) {
                return order;
            }
        }
        throw new InvalidOrderException("Order not found with the provided criteria.");
    }

    public ArrayList<Medicine> findItems(String name, String manufacturer, double dose,
                                         String unit, double unitPrice, String expirationDate)
            throws InvalidItemException {
        ArrayList<Medicine> foundItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof Medicine) {
                Medicine medicine = (Medicine) item;
                if (medicine.getName().equalsIgnoreCase(name)
                        && medicine.getManufacturer().equalsIgnoreCase(manufacturer)
                        && medicine.getDose() == dose
                        && medicine.getUnit().equals(unit)
                        && medicine.getUnitPrice() == unitPrice
                        && medicine.getExpirationDate().toString().equals(expirationDate)) {
                    foundItems.add(medicine);
                }
            }
        }
        if (foundItems.isEmpty()) {
            throw new InvalidItemException("No medicine found matching the criteria.");
        }
        return foundItems;
    }

    public ArrayList<Medicine> findExpiredMeds() {
        ArrayList<Medicine> expiredMeds = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof Medicine) {
                Medicine medicine = (Medicine) item;
                if (medicine.hasExpired()) {
                    expiredMeds.add(medicine);
                }
            }
        }
        return expiredMeds;
    }

    public ArrayList<MedicalAccessory> findItems(String name, String manufacturer,
                                                 String modelNo, double unitPrice)
            throws InvalidItemException {
        ArrayList<MedicalAccessory> foundItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item instanceof MedicalAccessory) {
                MedicalAccessory accessory = (MedicalAccessory) item;
                if (accessory.getName().equalsIgnoreCase(name)
                        && accessory.getManufacturer().equalsIgnoreCase(manufacturer)
                        && accessory.getModelNo().equals(modelNo)
                        && accessory.getUnitPrice() == unitPrice) {
                    foundItems.add(accessory);
                }
            }
        }
        if (foundItems.isEmpty()) {
            throw new InvalidItemException("No medical accessory found matching the criteria.");
        }
        return foundItems;
    }

    public ArrayList<Item> findItems(String name, String manufacturer) throws InvalidItemException {
        ArrayList<Item> foundItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equalsIgnoreCase(name)
                    && item.getManufacturer().equalsIgnoreCase(manufacturer)) {
                foundItems.add(item);
            }
        }
        if (foundItems.isEmpty()) {
            throw new InvalidItemException("No item found with the provided name and manufacturer.");
        }
        return foundItems;
    }

    // CHANGED: now throws InvalidItemException and rejects already-expired medicines
    public String addItem(String name, String manufacturer, double dose, String unit,
                          double unitPrice, int quantity, String expirationDate)
            throws InvalidItemException {
        Medicine medicine = new Medicine(name, manufacturer, dose, unit, unitPrice, quantity, expirationDate);
        if (medicine.hasExpired()) {
            throw new InvalidItemException(medicine.getName(), medicine.getManufacturer());
        }
        items.add(medicine);
        return medicine.getId();
    }

    public String addItem(String name, String manufacturer, String modelNo,
                          double unitPrice, int quantity) {
        MedicalAccessory accessory = new MedicalAccessory(name, manufacturer, modelNo, unitPrice, quantity);
        items.add(accessory);
        return accessory.getId();
    }

    public int acceptOrderRequest(String orderId) {
        try {
            Order order = findOrder(orderId);
            Item item = findItem(order.getItemId());
            int availableQuantity = item.getQuantity();
            int orderedQuantity = order.getQuantity();
            if (availableQuantity < orderedQuantity) {
                throw new OutOfStockException("Insufficient stock for item: " + item.getName());
            }
            item.decreaseQuantity(orderedQuantity);
            order.completeOrder();
            return item.getQuantity();
        } catch (InvalidItemException e) {
            System.out.println("Error: Item not found - " + e.getMessage());
        } catch (OutOfStockException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
        return -1;
    }

    public String orderItem(String medId, String customerContact, int quantity)
            throws InvalidItemException {
        Item item = findItem(medId);
        if (item instanceof Medicine) {
            Medicine medicine = (Medicine) item;
            if (medicine.hasExpired()) {
                throw new InvalidItemException(medicine.getId());
            }
        }
        Order order = new Order(medId, customerContact, quantity);
        order.addItem(item);
        requestedItems.add(order);
        return order.getOrderId();
    }

    public ArrayList<Order> getMyOrder(String customerContact) {
        ArrayList<Order> customerOrders = new ArrayList<>();
        for (int i = 0; i < requestedItems.size(); i++) {
            Order order = requestedItems.get(i);
            if (order.getOrderBy().equals(customerContact)) {
                customerOrders.add(order);
            }
        }
        return customerOrders;
    }

    public ArrayList<Order> getNewOrders() {
        ArrayList<Order> newOrders = new ArrayList<>();
        for (int i = 0; i < requestedItems.size(); i++) {
            Order order = requestedItems.get(i);
            if (order.getStatus().equals("New")) {
                newOrders.add(order);
            }
        }
        return newOrders;
    }
}
