package com.example.pharmafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LoginButtonController {

    @FXML
    private RadioButton CustomerChoice;

    @FXML
    private RadioButton EmployeeChoice;

    @FXML
    private Button LoginButton;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    void Login(ActionEvent event) {
        if (EmployeeChoice != null) {
            if (EmployeeChoice.isSelected()) {
                open(event, "EmployeeDashBoard.fxml");
                return;
            }
        }

        if (CustomerChoice != null) {
            if (CustomerChoice.isSelected()) {
                open(event, "CustomerDashBoard.fxml");
                return;
            }
        }
    }

    private void open(ActionEvent event, String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxml));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Could not load " + fxml + ": " + e.getMessage());
        }
    }
}
