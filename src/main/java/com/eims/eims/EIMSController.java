package com.eims.eims;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;

public class EIMSController {
    @FXML
    private Label welcomeText;

    @FXML
    protected TreeView<File> directoryTree;


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to EIMS Application!");
    }


    @FXML
    void handle(MouseEvent event) {

        TreeItem<File> selectedItem = directoryTree.getSelectionModel().getSelectedItem();
        try {
            addItems(selectedItem, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
//        if (selectedItem != null && selectedItem.getValue().isDirectory() && selectedItem.getChildren().isEmpty()) {
//            File selectedDir = selectedItem.getValue();
//            File[] subDirs = selectedDir.listFiles(File::isDirectory);
//            if (subDirs != null) {
//                for (File subDir : subDirs) {
//                    TreeItem<File> subDirItem = new TreeItem<>(subDir);
//                    subDirItem.setExpanded(false); // 设置初始展开状态为关闭
//                    selectedItem.getChildren().add(subDirItem);
//                }
//            }
//        }


        directoryTree.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {

                        if (!empty) {
                            super.updateItem(item, false);
                            HBox hBox = new HBox();
                            Label label = new Label(isListRoots(item));
                            this.setGraphic(hBox);
                            this.setStyle("-fx-border-color: rgb(244,244,244)");

                            hBox.getChildren().add(label);//把label加到hBox面板中

                        } else {

                            this.setGraphic(null);
                        }
                    }
                };
            }
        });



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

    public String isListRoots(File item) {

        File[] rootlist = File.listRoots();
        for (File isListRoots : rootlist) {
            if (item.toString().equals(isListRoots.toString())) {
                return item.toString();
            }
        }
        return item.getName();
    }

}