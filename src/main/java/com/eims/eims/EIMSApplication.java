package com.eims.eims;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;


public class EIMSApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(EIMSApplication.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 640);
        stage.setMinHeight(500);
        stage.setMinWidth(700);
        stage.setTitle("电子图片管理系统");
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch();
    }



}

