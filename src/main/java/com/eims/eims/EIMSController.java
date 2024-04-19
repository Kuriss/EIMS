package com.eims.eims;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class EIMSController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to EIMS Application!");
    }
}