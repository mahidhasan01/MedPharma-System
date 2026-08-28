package com.example.pharmafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class CustomerDashBoardController {

    @FXML
    private Button Logout;

    @FXML
    private Button PayorViewMyOrders;

    @FXML
    private Button SearchOrderItem;

    @FXML
    private AnchorPane scenePane;

    @FXML
    void LogOut(ActionEvent event) {
        open(event, "LoginPage.fxml");
    }

    @FXML
    void PayorViewOrders(ActionEvent event) {
        open(event, "PayOrViewMyOrder.fxml");
    }

    @FXML
    void SearchOrOrder(ActionEvent event) {
        open(event, "ViewOrRequestOrders.fxml");
    }

    private void open(ActionEvent e, String fxml) {
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
