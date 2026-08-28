package com.example.pharmafx;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import med.lib.InvalidItemException;
import med.lib.Item;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDashBoardController {

    @FXML private Button AddItem;
    @FXML private Button ViewAcceptOrder;
    @FXML private Button FindExpMed;
    @FXML private Button LogOut;

    @FXML private ComboBox<String> Type;
    @FXML private ComboBox<String> Manufacturer;
    @FXML private TextField Name;
    @FXML private ListView<String> SuggestionBox1;

    private static final String MF_FILE = "manufacturers.txt";

    @FXML
    public void initialize() {

        Type.setItems(FXCollections.observableArrayList("Medicine", "MedicalAccessory"));
        Type.setOnAction(e -> runSearch());

        Manufacturer.setEditable(true);
        Manufacturer.setItems(FXCollections.observableArrayList(loadManufacturers()));
        Manufacturer.setOnAction(e -> runSearch());

        Name.setOnAction(e -> runSearch());
        Name.textProperty().addListener((a, b, c) -> runSearch());

        SuggestionBox1.setItems(FXCollections.observableArrayList());
    }

    @FXML
    public void LogOut(ActionEvent e) {
        open(e, "LoginPage.fxml");
    }

    @FXML
    public void AddItem(ActionEvent e) {
        open(e, "AddMedicineOrAccessory.fxml");
    }

    @FXML
    public void ViewAcceptOrder(ActionEvent e) {
        open(e, "ViewOrAcceptOrder.fxml");
    }

    @FXML
    public void FindExpMed(ActionEvent e) {
        open(e, "ExpiredMedicineList.fxml");
    }

    @FXML
    public void Search() {
        runSearch();
    }

    private void runSearch() {
        String name = safeText(Name);
        String mfg  = safeCombo(Manufacturer);
        String type = safeSelected(Type);

        if (name.length() == 0 && mfg.length() == 0) {
            SuggestionBox1.setItems(FXCollections.observableArrayList("Enter Name and Manufacturer to search."));
            return;
        }

        try {
            ArrayList<Item> found = Records.pharma.findItems(name, mfg);
            ArrayList<String> lines = new ArrayList<>();

            int i = 0;
            while (i < found.size()) {
                Item it = found.get(i);

                boolean matchesType = true;
                if (type.length() > 0) {
                    String cls = it.getClass().getSimpleName();
                    if (!cls.equalsIgnoreCase(type)) {
                        matchesType = false;
                    }
                }

                if (matchesType) {
                    String row = it.getName() + "  [" + it.getId() + "]"  + "  " + it.getManufacturer()  + "  Qty: " + it.getQuantity()  + "  Price: " + it.getUnitPrice();

                    lines.add(row);
                }

                i = i + 1;
            }

            if (lines.isEmpty()) {
                SuggestionBox1.setItems(FXCollections.observableArrayList("No results."));
            } else {
                SuggestionBox1.setItems(FXCollections.observableArrayList(lines));
            }
        } catch (InvalidItemException ex) {
            SuggestionBox1.setItems(FXCollections.observableArrayList("Error: " + ex.getMessage()));
        } catch (Exception ex) {
            SuggestionBox1.setItems(FXCollections.observableArrayList("Error: " + ex.getMessage()));
        }
    }

    private String safeText(TextField tf) {
        if (tf == null) {
            return "";
        }
        String s = tf.getText();
        if (s == null) {
            return "";
        }
        return s.trim();
    }

    private String safeCombo(ComboBox<String> cb) {
        if (cb == null) {
            return "";
        }

        String v = null;

        if (cb.getSelectionModel() != null) {
            v = cb.getSelectionModel().getSelectedItem();
        }

        if (v == null) {
            if (cb.getEditor() != null) {
                v = cb.getEditor().getText();
            }
        }

        if (v == null) {
            return "";
        }

        return v.trim();
    }

    private String safeSelected(ComboBox<String> cb) {
        if (cb == null) {
            return "";
        }

        if (cb.getSelectionModel() == null) {
            return "";
        }

        String v = cb.getSelectionModel().getSelectedItem();
        if (v == null) {
            return "";
        }

        return v.trim();
    }

    private ArrayList<String> loadManufacturers() {
        ArrayList<String> out = new ArrayList<>();

        try {
            Path p = Path.of(MF_FILE);
            if (!Files.exists(p)) {
                Files.createFile(p);
                return out;
            }

            List<String> all = Files.readAllLines(p);

            int i = 0;
            while (i < all.size()) {
                String s = all.get(i);

                if (s != null) {
                    String t = s.trim();

                    if (t.length() > 0) {
                        if (!inList(out, t)) {
                            out.add(t);
                        }
                    }
                }

                i = i + 1;
            }
        } catch (Exception ignored) {
        }

        return out;
    }

    private boolean inList(ArrayList<String> list, String value) {
        int i = 0;
        while (i < list.size()) {
            String s = list.get(i);
            if (s.equals(value)) {
                return true;
            }
            i = i + 1;
        }
        return false;
    }

    private void open(ActionEvent e, String fxml) {
        try {
            FXMLLoader fx = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene sc = new Scene(fx.load());

            Stage st = (Stage) ((Node) e.getSource()).getScene().getWindow();
            st.setScene(sc);
            st.show();
        } catch (Exception ex) {
            System.out.println("Could not load " + fxml + ": " + ex.getMessage());
        }
    }
}
