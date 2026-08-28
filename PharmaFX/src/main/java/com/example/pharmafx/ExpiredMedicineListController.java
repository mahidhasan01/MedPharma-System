package com.example.pharmafx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import med.lib.Medicine;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExpiredMedicineListController {

    @FXML
    private Button backBtn;

    @FXML
    private TableView<Row> expiredTable;

    @FXML
    private TableColumn<Row, String> colName;

    @FXML
    private TableColumn<Row, String> colManufacturer;

    @FXML
    private TableColumn<Row, String> colDose;

    @FXML
    private TableColumn<Row, String> colUnit;

    @FXML
    private TableColumn<Row, Double> colUnitPrice;

    @FXML
    private TableColumn<Row, Integer> colQuantity;

    @FXML
    private TableColumn<Row, String> colExpiryDate;

    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name));
        colManufacturer.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().manufacturer));
        colDose.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().dose));
        colUnit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().unit));
        colUnitPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().unitPrice).asObject());
        colQuantity.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().quantity).asObject());
        colExpiryDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().expiry));
        refresh();
    }

    @FXML
    void goBack(ActionEvent event) {
        changeScene(event, "EmployeeDashBoard.fxml");
    }

    private void refresh() {
        try {
            ArrayList<Medicine> list = Records.pharma.findExpiredMeds();
            ArrayList<Row> rows = new ArrayList<>();

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            int i = 0;
            while (i < list.size()) {
                Medicine m = list.get(i);

                String d = String.valueOf(m.getDose());
                String u = m.getUnit();

                String e = "";
                if (m.getExpirationDate() != null) {
                    e = m.getExpirationDate().format(fmt);
                }

                Row r = new Row(m.getName(), m.getManufacturer(), d, u, m.getUnitPrice(), m.getQuantity(), e);

                rows.add(r);

                i = i + 1;
            }

            expiredTable.setItems(FXCollections.observableArrayList(rows));
        } catch (Exception e) {
            expiredTable.setItems(FXCollections.observableArrayList());
        }
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

    public static class Row {
        final String name,manufacturer,dose,unit,expiry;
        final double unitPrice;
        final int quantity;

        Row(String name, String manufacturer, String dose, String unit, double unitPrice, int quantity, String expiry) {

            this.name = name;
            this.manufacturer = manufacturer;
            this.dose = dose;
            this.unit = unit;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.expiry = expiry;
        }
    }
}
