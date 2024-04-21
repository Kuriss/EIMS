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
        stage.setTitle("电子图片管理系统");
        stage.setScene(scene);

        EIMSController control=fxmlLoader.getController();//获取eimscontroller的具体变量，然后调用里面的控件



        File[] items = File.listRoots();//
        TreeItem<File> mainTreeItem = new TreeItem<>();

        for(File item:items){
            if(item.isDirectory()){
                TreeItem<File> treeItem = new TreeItem<>(item);
                mainTreeItem.getChildren().add(treeItem);
                addItems(treeItem,0);

            }
        }
       // control.directoryTree = new TreeView<>(mainTreeItem);
        control.directoryTree.setRoot(mainTreeItem);
        control.directoryTree.setShowRoot(false);



        stage.show();
    }

    public void addItems(TreeItem<File> in, int flag) throws IOException {
        File[] fileList = null;
        if(in!=null) {
            fileList = in.getValue().listFiles();
        }
        if (fileList != null) {
            if (flag == 0) {
                //remove(from,to) 移除[from,to)之间的元素,from是包含,to是不包含的,注意与remove监听事件返回的from和to都是相同的
                //这里移除是为了之后我们还会将这个节点加进去，否者会重复
                //System.out.println(in.getChildren().size());
                in.getChildren().remove(0, in.getChildren().size());
            }

            if (fileList.length > 0) {
                for (File file : fileList) {
                    if (file.isDirectory() & !file.isHidden()) {
                        TreeItem<File> newItem = new TreeItem<>(file);
                        if (flag < 1) {
                            //flag小与1后我们就一共调用两次addItems函数，就层数只有2
                            addItems(newItem, flag + 1);
                        }
                        in.getChildren().add(newItem);
                    }
                }
            }
        }
    }



    public static void main(String[] args) {
        launch();
    }



}

