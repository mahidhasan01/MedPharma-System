package med.app;

import med.lib.InvalidItemException;
import med.lib.InvalidOrderException;
import med.lib.MedPharma;
import med.lib.Order;

import java.util.Scanner;

public class PharmaApp {

    private static final String DATA_FILE = "records.txt";

    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);
        MedPharma pharma = MedPharma.load(DATA_FILE);
        int role = -1;

        while (role != 0) {
            System.out.println("Enter 1 for employee, 2 for customer, 3 to switch role, 0 to exit.");
            role = s.nextInt();

            switch (role) {
                case 1:
                    int employeeChoice = -1;
                    while (employeeChoice != 0) {
                        System.out.println("\nEmployee Menu:");
                        System.out.println("1. Add Medicine");
                        System.out.println("2. Add Accessory");
                        System.out.println("3. View Orders");
                        System.out.println("4. Accept Order");
                        System.out.println("5. Search Items");
                        System.out.println("6. Find Expired Medicines");
                        System.out.println("0. Logout");
                        employeeChoice = s.nextInt();

                        switch (employeeChoice) {
                            case 1:
                                System.out.print("Name: ");
                                String name = s.next();
                                System.out.print("Manufacturer: ");
                                String manufacturer = s.next();
                                System.out.print("Dose: ");
                                double dose = s.nextDouble();
                                System.out.print("Unit: ");
                                String unit = s.next();
                                System.out.print("Unit Price: ");
                                double unitPrice = s.nextDouble();
                                System.out.print("Quantity: ");
                                int quantity = s.nextInt();
                                System.out.print("Expiration Date (dd/MM/yyyy): ");
                                String expirationDate = s.next();

                                try {
                                    String itemId = pharma.addItem(
                                            name,
                                            manufacturer,
                                            dose,
                                            unit,
                                            unitPrice,
                                            quantity,
                                            expirationDate
                                    );
                                    System.out.println("Medicine added with ID: " + itemId);
                                    pharma.saveToFile(DATA_FILE);
                                } catch (InvalidItemException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 2:
                                System.out.print("Name: ");
                                name = s.next();
                                System.out.print("Manufacturer: ");
                                manufacturer = s.next();
                                System.out.print("Model No: ");
                                String modelNo = s.next();
                                System.out.print("Unit Price: ");
                                unitPrice = s.nextDouble();
                                System.out.print("Quantity: ");
                                quantity = s.nextInt();
                                String itemId = pharma.addItem(name, manufacturer, modelNo, unitPrice, quantity);
                                System.out.println("Accessory added with ID: " + itemId);
                                pharma.saveToFile(DATA_FILE);
                                break;

                            case 3:
                                for (int i = 0; i < pharma.getNewOrders().size(); i++) {
                                    System.out.println(pharma.getNewOrders().get(i));
                                }
                                break;

                            case 4:
                                System.out.print("Enter Order ID: ");
                                String orderId = s.next();
                                int finalQuantity = pharma.acceptOrderRequest(orderId);
                                if (finalQuantity != -1) {
                                    System.out.println("Order accepted. Final item quantity: " + finalQuantity);
                                    pharma.saveToFile(DATA_FILE);
                                } else {
                                    System.out.println("Failed to accept the order.");
                                }
                                break;

                            case 5:
                                System.out.print("Name: ");
                                name = s.next();
                                System.out.print("Manufacturer: ");
                                manufacturer = s.next();
                                try {
                                    for (int i = 0; i < pharma.findItems(name, manufacturer).size(); i++) {
                                        System.out.println(pharma.findItems(name, manufacturer).get(i));
                                    }
                                } catch (InvalidItemException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 6:
                                for (int i = 0; i < pharma.findExpiredMeds().size(); i++) {
                                    System.out.println(pharma.findExpiredMeds().get(i));
                                }
                                break;

                            case 0:
                                System.out.println("Logging out...");
                                break;

                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                    }
                    break;

                case 2:
                    int customerChoice = -1;
                    while (customerChoice != 0) {
                        System.out.println("\nCustomer Menu:");
                        System.out.println("1. Request Order");
                        System.out.println("2. Search Items");
                        System.out.println("3. View Order");
                        System.out.println("4. Pay Bill");
                        System.out.println("0. Logout");
                        customerChoice = s.nextInt();

                        switch (customerChoice) {
                            case 1:
                                System.out.print("Item ID: ");
                                String itemId2 = s.next();
                                System.out.print("Quantity: ");
                                int quantity2 = s.nextInt();
                                System.out.print("Contact Info: ");
                                String customerContact = s.next();
                                try {
                                    String orderId2 = pharma.orderItem(itemId2, customerContact, quantity2);
                                    System.out.println("Order placed with Order ID: " + orderId2);
                                    pharma.saveToFile(DATA_FILE);
                                } catch (InvalidItemException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 2:
                                System.out.print("Name: ");
                                String name2 = s.next();
                                System.out.print("Manufacturer: ");
                                String manufacturer2 = s.next();
                                try {
                                    for (int i = 0; i < pharma.findItems(name2, manufacturer2).size(); i++) {
                                        System.out.println(pharma.findItems(name2, manufacturer2).get(i));
                                    }
                                } catch (InvalidItemException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 3:
                                System.out.print("Enter Order ID: ");
                                String orderId3 = s.next();
                                try {
                                    System.out.println(pharma.findOrder(orderId3));
                                } catch (InvalidOrderException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 4:
                                System.out.print("Enter Order ID: ");
                                String orderId4 = s.next();
                                try {
                                    Order order = pharma.findOrder(orderId4);
                                    System.out.println("Total Bill: " + order.getTotalBill());
                                    System.out.println("Payment Successful.");
                                } catch (InvalidOrderException e) {
                                    System.out.println("Error: " + e.getMessage());
                                }
                                break;

                            case 0:
                                System.out.println("Logging out...");
                                break;

                            default:
                                System.out.println("Invalid option. Please try again.");
                        }
                    }
                    break;

                case 3:
                    System.out.println("Switching role...");
                    role = switchRole(role);
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        pharma.saveToFile(DATA_FILE);
        s.close();
    }

    private static int switchRole(int currentRole) {
        if (currentRole == 1) {
            System.out.println("Switched to Customer Role.");
            return 2;
        } else if (currentRole == 2) {
            System.out.println("Switched to Employee Role.");
            return 1;
        }
        return currentRole;
    }
}
