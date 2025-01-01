package com.example.tilemapgenerator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;


public class HelloController {


    public Button btnRestSetup;
    public Button btnSaveSetup;
    public Text tvMapSizePixels;
    public int gridColumnCount;
    public int gridRowCount;

    @FXML
    public void saveMapInfo(){

    }

    public void switchScene(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/com/example/tilemapgenerator/main.fxml"));
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void resetMapInfo(ActionEvent actionEvent) {
    }
}