package com.example.pharmafx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import med.lib.InvalidItemException;
import med.lib.Item;
import med.lib.Order;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ViewOrAcceptOrderController {

    @FXML
    private Button AcceptOrder;

    @FXML
    private TextField OrderIDField;

    @FXML
    private TableView<OrderRow> OrdersTable;

    @FXML
    private TableColumn<OrderRow, String> colId;

    @FXML
    private TableColumn<OrderRow, String> colItemName;

    @FXML
    private TableColumn<OrderRow, String> colOrderDate;

    @FXML
    private TableColumn<OrderRow, String> colOrderStatus;

    @FXML
    private TableColumn<OrderRow, Integer> colQty;

    @FXML
    private TableColumn<OrderRow, Double> colUnitPrice;

    @FXML
    private TableColumn<OrderRow, Double> colTotal;

    @FXML
    private Button BackButton;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getId()));
        colItemName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getItemName()));
        colOrderDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getOrderDate()));
        colOrderStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQty()).asObject());
        colUnitPrice.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getUnitPrice()).asObject());
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotal()).asObject());

        if (BackButton != null) {
            BackButton.setOnAction(this::Back);
        }

        if (OrdersTable != null && OrderIDField != null) {
            OrdersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
                if (newRow != null) {
                    OrderIDField.setText(newRow.getId());
                }
            });
        }

        refreshTable();
    }

    @FXML
    void AcceptOrder(ActionEvent event) {
        try {
            String id = OrderIDField.getText().trim();

            if (id.length() == 0) {
                info("Enter an Order ID.");
                return;
            }

            int left = Records.pharma.acceptOrderRequest(id);

            if (left != -1) {
                Records.save();
                info("Order accepted.\nItems left in stock: " + left);
                refreshTable();
            } else {
                info("Could not accept order. Check the Order ID.");
            }
        } catch (Exception e) {
            info("Could not accept order.");
        }
    }
    @FXML
    void Back(ActionEvent e) {
        changeScene(e, "EmployeeDashBoard.fxml");
    }

    private void refreshTable() {
        try {
            ArrayList<Order> list = Records.pharma.getNewOrders();
            ArrayList<OrderRow> rows = new ArrayList<>();

            int i = 0;
            while (i < list.size()) {
                Order o = list.get(i);

                String id = o.getOrderId();
                String status = o.getStatus();
                String date = "";
                if (o.getOrderDate() != null) {
                    date = o.getOrderDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                }
                int qty = o.getQuantity();

                String itemName = "";
                double unitPrice = 0.0;
                double total = 0.0;

                try {
                    Item it = Records.pharma.findItem(o.getItemId());
                    itemName = it.getName();
                    unitPrice = it.getUnitPrice();
                    total = unitPrice * qty;
                } catch (InvalidItemException ignore) {
                }

                rows.add(new OrderRow(id, itemName, date, status, qty, unitPrice, total));

                i = i + 1;
            }

            OrdersTable.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            OrdersTable.setItems(FXCollections.observableArrayList());
        }
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void changeScene(ActionEvent e, String fxml) {
        try {
            FXMLLoader fx = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene sc = new Scene(fx.load());

            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();

            st.setScene(sc);
            st.show();
        } catch (Exception ignored) {
        }
    }

    public static class OrderRow {

        private final String id;
        private final String itemName;
        private final String orderDate;
        private final String status;
        private final int qty;
        private final double unitPrice;
        private final double total;

        public OrderRow(String id,
                        String itemName,
                        String orderDate,
                        String status,
                        int qty,
                        double unitPrice,
                        double total) {
            this.id = id;
            this.itemName = itemName;
            this.orderDate = orderDate;
            this.status = status;
            this.qty = qty;
            this.unitPrice = unitPrice;
            this.total = total;
        }

        public String getId() {
            return id;
        }

        public String getItemName() {
            return itemName;
        }

        public String getOrderDate() {
            return orderDate;
        }

        public String getStatus() {
            return status;
        }

        public int getQty() {
            return qty;
        }

        public double getUnitPrice() {
            return unitPrice;
        }

        public double getTotal() {
            return total;
        }
    }
}
