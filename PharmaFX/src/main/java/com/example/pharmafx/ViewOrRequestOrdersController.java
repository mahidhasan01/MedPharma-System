package com.example.pharmafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import med.lib.InvalidItemException;
import med.lib.Item;
import med.lib.MedicalAccessory;
import med.lib.Medicine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ViewOrRequestOrdersController {

    @FXML
    private TextField ItemID;

    @FXML
    private TextField Quantity;

    @FXML
    private TextField Contact;

    @FXML
    private ComboBox<String> Type;

    @FXML
    private ComboBox<String> Manufacturer;

    @FXML
    private ComboBox<String> Name;

    @FXML
    private ListView<String> SuggestionList1;

    @FXML
    private Button RequestOrderButton;

    @FXML
    private Button BackButton;

    private static final String MF_FILE = "manufacturers.txt";

    @FXML
    public void initialize() {
        ObservableList<String> types = FXCollections.observableArrayList();
        types.add("Medicine");
        types.add("Medical Accessory");

        if (Type != null) {
            Type.setItems(types);
            Type.setOnAction(e -> search());
        }

        if (Manufacturer != null) {
            Manufacturer.setEditable(true);
            Manufacturer.setItems(FXCollections.observableArrayList(loadManufacturers()));
            Manufacturer.setOnAction(e -> search());
            Manufacturer.getEditor().textProperty().addListener((obs, o, n) -> search());
        }

        if (Name != null) {
            Name.setEditable(true);
            Name.setOnAction(e -> search());
            Name.getEditor().textProperty().addListener((obs, o, n) -> search());
        }

        if (SuggestionList1 != null) {
            SuggestionList1.setItems(FXCollections.observableArrayList());
            SuggestionList1.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
                if (n != null) {
                    String id = extractId(n);
                    if (!id.isEmpty() && ItemID != null) {
                        ItemID.setText(id);
                    }
                }
            });
        }

        if (BackButton != null) {
            BackButton.setOnAction(this::goBack);
        }
    }

    @FXML
    public void RequestOrder(ActionEvent event) {
        try {
            String id = safeText(ItemID);
            String qtyText = safeText(Quantity);
            String contact = safeText(Contact);

            if (id.isEmpty()) {
                info("Enter Item ID.");
                return;
            }

            if (qtyText.isEmpty()) {
                info("Enter quantity.");
                return;
            }

            if (contact.isEmpty()) {
                info("Enter contact.");
                return;
            }

            int qty = Integer.parseInt(qtyText);

            String orderId = Records.pharma.orderItem(id, contact, qty);
            Records.save();

            info("Order placed.\nOrder ID: " + orderId);

            ItemID.clear();
            Quantity.clear();
            Contact.clear();
        } catch (NumberFormatException ex) {
            info("Quantity must be a number.");
        } catch (InvalidItemException ex) {
            info("Item not found or cannot be ordered.");
        } catch (Exception ex) {
            info("Could not place order.");
        }
    }

    private void search() {
        try {
            String nameFilter = safeEditor(Name).toLowerCase();
            String makerFilter = safeEditor(Manufacturer).toLowerCase();
            String typeFilter = safeType();

            ArrayList<Item> all = Records.pharma.getAllItems();
            ArrayList<Item> results = new ArrayList<>();

            for (int i = 0; i < all.size(); i++) {
                Item it = all.get(i);

                if (it instanceof Medicine) {
                    Medicine m = (Medicine) it;
                    if (m.hasExpired()) {
                        continue;
                    }
                }

                boolean matches = true;

                if (!typeFilter.isEmpty()) {
                    if ("Medicine".equals(typeFilter)) {
                        if (!(it instanceof Medicine)) {
                            matches = false;
                        }
                    } else if ("Medical Accessory".equals(typeFilter)) {
                        if (!(it instanceof MedicalAccessory)) {
                            matches = false;
                        }
                    }
                }

                if (matches && !makerFilter.isEmpty()) {
                    String m = it.getManufacturer();
                    if (m == null || !m.toLowerCase().contains(makerFilter)) {
                        matches = false;
                    }
                }

                if (matches && !nameFilter.isEmpty()) {
                    String n = it.getName();
                    if (n == null || !n.toLowerCase().contains(nameFilter)) {
                        matches = false;
                    }
                }

                if (matches) {
                    results.add(it);
                }
            }

            ObservableList<String> lines = FXCollections.observableArrayList();
            ObservableList<String> nameOptions = FXCollections.observableArrayList();

            for (int j = 0; j < results.size(); j++) {
                Item it = results.get(j);

                String row = it.getId()
                        + "  |  " + it.getName()
                        + "  |  " + it.getManufacturer()
                        + "  |  " + "Price: " + it.getUnitPrice()
                        + "  |  " + "Qty: " + it.getQuantity();

                lines.add(row);

                String nm = it.getName();
                boolean exists = false;
                for (int k = 0; k < nameOptions.size(); k++) {
                    if (nameOptions.get(k).equals(nm)) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    nameOptions.add(nm);
                }
            }

            if (results.isEmpty()) {
                lines.add("No items found for these filters.");
            }

            SuggestionList1.setItems(lines);

            if (Name != null) {
                Name.setItems(nameOptions);
            }
        } catch (Exception ex) {
            SuggestionList1.setItems(FXCollections.observableArrayList("Error while searching."));
            if (Name != null) {
                Name.setItems(FXCollections.observableArrayList());
            }
        }
    }

    private String extractId(String row) {
        int p = row.indexOf(" ");
        if (p == -1) {
            return row.trim();
        }
        return row.substring(0, p).trim();
    }

    private void goBack(ActionEvent e) {
        open(e, "CustomerDashBoard.fxml");
    }

    private void open(ActionEvent e, String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (Exception ignored) {
        }
    }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private String safeText(TextField tf) {
        if (tf == null) return "";
        String s = tf.getText();
        if (s == null) return "";
        return s.trim();
    }

    private String safeEditor(ComboBox<String> cb) {
        if (cb == null) return "";
        if (cb.getEditor() == null) return "";
        String s = cb.getEditor().getText();
        if (s == null) return "";
        return s.trim();
    }

    private String safeType() {
        if (Type == null) return "";
        if (Type.getValue() == null) return "";
        return Type.getValue().trim();
    }

    private ArrayList<String> loadManufacturers() {
        ArrayList<String> out = new ArrayList<>();

        try {
            Path p = Path.of(MF_FILE);
            if (!Files.exists(p)) {
                return out;
            }

            List<String> all = Files.readAllLines(p);

            for (int i = 0; i < all.size(); i++) {
                String s = all.get(i);
                if (s != null) {
                    String t = s.trim();
                    if (!t.isEmpty()) {
                        if (!inList(out, t)) {
                            out.add(t);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return out;
    }

    private boolean inList(ArrayList<String> list, String value) {
        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            if (s.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
