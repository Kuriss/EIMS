package com.eims.eims;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;


public class EIMSApplication extends Application {
    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(EIMSApplication.class.getResource("MainWindow.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setMinHeight(500);
            stage.setMinWidth(700);
            //设置程序图标
            stage.getIcons().add(new Image("picIcon2.png"));
            //隐藏顶部装饰栏
            stage.initStyle(StageStyle.UNDECORATED);

            // 监听鼠标按下事件
            scene.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            // 监听鼠标拖拽事件
            scene.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            stage.setTitle("电子图片管理系统");
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}

