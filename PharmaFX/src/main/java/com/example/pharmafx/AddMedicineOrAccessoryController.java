package com.example.pharmafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddMedicineOrAccessoryController {

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSubmitAcc;

    @FXML
    private Button btnSubmitMed;

    @FXML
    private DatePicker dateExpiration;

    @FXML
    private TextField txtAccessoryName;

    @FXML
    private TextField txtManufacturerAcc;

    @FXML
    private TextField txtModelNo;

    @FXML
    private TextField txtUnitPriceAcc;

    @FXML
    private TextField txtQuantityAcc;

    @FXML
    private TextField txtMedicineName;

    @FXML
    private TextField txtManufacturerMed;

    @FXML
    private TextField txtDose;

    @FXML
    private TextField txtUnitMed;

    @FXML private TextField txtUnitPriceMed;

    @FXML
    private TextField txtQuantityMed;

    private static final String MF_FILE = "manufacturers.txt";

    @FXML
    void onBack(ActionEvent event) {
        changeScene(event, "EmployeeDashBoard.fxml");
    }

    @FXML
    void onSubmitAccessory(ActionEvent event) {
        try {
            String name = txtAccessoryName.getText().trim();
            String mfg = txtManufacturerAcc.getText().trim();
            String model = txtModelNo.getText().trim();
            String priceS = txtUnitPriceAcc.getText().trim();
            String qtyS = txtQuantityAcc.getText().trim();

            if (name.isEmpty()) {
                info("Enter accessory name.");
                return;
            }

            if (mfg.isEmpty()) {
                info("Enter manufacturer.");
                return;
            }

            if (model.isEmpty()) {
                info("Enter model no.");
                return;
            }

            if (priceS.isEmpty()) {
                info("Enter unit price.");
                return;
            }

            if (qtyS.isEmpty()) {
                info("Enter quantity.");
                return;
            }

            double price = Double.parseDouble(priceS);
            int qty = Integer.parseInt(qtyS);

            String id = Records.pharma.addItem(name, mfg, model, price, qty);
            Records.save();
            addManufacturerIfNeeded(mfg);

            txtAccessoryName.clear();
            txtManufacturerAcc.clear();
            txtModelNo.clear();
            txtUnitPriceAcc.clear();
            txtQuantityAcc.clear();

            info("Accessory added.\nID: " + id);
        } catch (NumberFormatException nfe) {
            info("Enter valid numbers for price and quantity.");
        } catch (Exception ex) {
            info("Could not add accessory.");
        }
    }

    @FXML
    void onSubmitMedicine(ActionEvent event) {
        try {
            String name = txtMedicineName.getText().trim();
            String mfg = txtManufacturerMed.getText().trim();
            String doseS = txtDose.getText().trim();
            String unit = txtUnitMed.getText().trim();
            String priceS = txtUnitPriceMed.getText().trim();
            String qtyS = txtQuantityMed.getText().trim();

            if (name.isEmpty()) {
                info("Enter medicine name.");
                return;
            }

            if (mfg.isEmpty()) {
                info("Enter manufacturer.");
                return;
            }

            if (doseS.isEmpty()) {
                info("Enter dose.");
                return;
            }

            if (unit.isEmpty()) {
                info("Enter unit.");
                return;
            }

            if (priceS.isEmpty()) {
                info("Enter unit price.");
                return;
            }

            if (qtyS.isEmpty()) {
                info("Enter quantity.");
                return;
            }

            if (dateExpiration.getValue() == null) {
                info("Select expiration date.");
                return;
            }

            double dose = Double.parseDouble(doseS);
            double price = Double.parseDouble(priceS);
            int qty = Integer.parseInt(qtyS);

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String exp = dateExpiration.getValue().format(fmt);

            String id = Records.pharma.addItem(name, mfg, dose, unit, price, qty, exp);
            Records.save();
            addManufacturerIfNeeded(mfg);

            txtMedicineName.clear();
            txtManufacturerMed.clear();
            txtDose.clear();
            txtUnitMed.clear();
            txtUnitPriceMed.clear();
            txtQuantityMed.clear();
            dateExpiration.setValue(null);

            info("Medicine added.\nID: " + id);
        } catch (NumberFormatException nfe) {
            info("Enter valid numbers for dose, price, and quantity.");
        } catch (Exception ex) {
            info("Could not add medicine.");
        }
    }

    private void addManufacturerIfNeeded(String manufacturer) {
        if (manufacturer == null) {
            return;
        }

        String m = manufacturer.trim();
        if (m.isEmpty()) {
            return;
        }

        try {
            Path p = Path.of(MF_FILE);

            if (!Files.exists(p)) {
                Files.createFile(p);
            }

            List<String> all = Files.readAllLines(p);

            boolean exists = false;
            for (String s : all) {
                if (s != null && s.trim().equalsIgnoreCase(m)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                Files.write(
                        p,
                        (m + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                        StandardOpenOption.APPEND
                );
            }
        } catch (Exception ignored) {
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
}
