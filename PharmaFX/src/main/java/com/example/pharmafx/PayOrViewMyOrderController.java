package com.example.pharmafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import med.lib.Order;

import java.util.ArrayList;

public class PayOrViewMyOrderController {

    @FXML
    private TableView<OrderRow> myOrdersTable;

    @FXML
    private TableColumn<OrderRow, String> colOrderId;

    @FXML
    private TableColumn<OrderRow, String> colItemId;

    @FXML
    private TableColumn<OrderRow, String> colInformation;

    @FXML
    private TableColumn<OrderRow, String> colQuantity;

    @FXML
    private TableColumn<OrderRow, String> colStatus;

    @FXML
    private TableColumn<OrderRow, String> colOrderDate;

    @FXML
    private TableColumn<OrderRow, String> colDeliveryDate;

    @FXML
    private TextField orderIdField;

    @FXML
    private Button viewOrderBtn;

    @FXML
    private Button payBillBtn;

    @FXML
    private Button BackButton;

    @FXML
    public void initialize() {
        if (colOrderId != null) {
            colOrderId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().orderId));
        }
        if (colItemId != null) {
            colItemId.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().itemId));
        }
        if (colInformation != null) {
            colInformation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().info));
        }
        if (colQuantity != null) {
            colQuantity.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().quantity));
        }
        if (colStatus != null) {
            colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        }
        if (colOrderDate != null) {
            colOrderDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().orderDate));
        }
        if (colDeliveryDate != null) {
            colDeliveryDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().deliveryDate));
        }
    }

    @FXML
    public void ViewMyOrder(ActionEvent e) {
        String contact = "";

        if (orderIdField != null) {
            contact = orderIdField.getText();
        }

        if (contact == null) {
            showInfo("Please enter contact info.");
            return;
        }

        contact = contact.trim();

        if (contact.length() == 0) {
            showInfo("Please enter contact info.");
            return;
        }

        try {
            ArrayList<Order> list = Records.pharma.getMyOrder(contact);
            ObservableList<OrderRow> rows = FXCollections.observableArrayList();

            for (int i = 0; i < list.size(); i++) {
                Order o = list.get(i);

                String orderDateStr = "";
                if (o.getOrderDate() != null) {
                    orderDateStr = o.getOrderDate().toString();
                }

                String deliveryDateStr = "";
                if (o.getDeliveryDate() != null) {
                    deliveryDateStr = o.getDeliveryDate().toString();
                }

                rows.add(new OrderRow(
                        o.getOrderId(),
                        o.getItemId(),
                        o.getOrderBy(),
                        String.valueOf(o.getQuantity()),
                        o.getStatus(),
                        orderDateStr,
                        deliveryDateStr
                ));
            }

            if (rows.isEmpty()) {
                showInfo("No orders found for " + contact + ".");
                myOrdersTable.setItems(FXCollections.observableArrayList());
            } else {
                myOrdersTable.setItems(rows);
            }
        } catch (Exception ex) {
            showInfo("Could not load orders.");
        }
    }

    @FXML
    public void ToPayBill(ActionEvent e) {
        String contact = "";

        if (orderIdField != null) {
            contact = orderIdField.getText();
        }

        if (contact == null) {
            showInfo("Please enter contact info.");
            return;
        }

        contact = contact.trim();

        if (contact.length() == 0) {
            showInfo("Please enter contact info.");
            return;
        }

        try {
            ArrayList<Order> list = Records.pharma.getMyOrder(contact);
            double total = 0.0;

            for (int i = 0; i < list.size(); i++) {
                Order o = list.get(i);
                if ("Complete".equalsIgnoreCase(o.getStatus())) {
                    total += o.getTotalBill();
                }
            }

            if (total <= 0.0) {
                showInfo("No new orders to pay for : " + contact + ".");
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                Order o = list.get(i);
                if ("Complete".equalsIgnoreCase(o.getStatus())) {
                    o.setStatus("Paid");
                }
            }

            Records.save();

            showInfo("Total Bill for " + contact + ": " + total + "\nPayment Successful.");

            ViewMyOrder(e);
        } catch (Exception ex) {
            showInfo("Could not pay bill.");
        }
    }

    @FXML
    public void Back(ActionEvent e) {
        try {
            Stage stage = (Stage) BackButton.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("CustomerDashBoard.fxml"));
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Customer Dashboard");
            stage.centerOnScreen();
        } catch (Exception ex) {
            showInfo("Could not go back to dashboard.");
        }
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class OrderRow {

        public final String orderId;
        public final String itemId;
        public final String info;
        public final String quantity;
        public final String status;
        public final String orderDate;
        public final String deliveryDate;

        public OrderRow(String orderId,
                        String itemId,
                        String info,
                        String quantity,
                        String status,
                        String orderDate,
                        String deliveryDate) {
            this.orderId = orderId;
            this.itemId = itemId;
            this.info = info;
            this.quantity = quantity;
            this.status = status;
            this.orderDate = orderDate;
            this.deliveryDate = deliveryDate;
        }
    }
}
