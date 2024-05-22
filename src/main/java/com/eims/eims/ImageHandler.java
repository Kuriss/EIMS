package com.eims.eims;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ImageHandler {

    private static FlowPane flowPane;//所在布局
    private ImageInDirectory imageInDirectory;//图片类对象
    private Integer selectSize = 0;//选中图片数量
    private Double sizeOfImage = 0.0;//图片大小
    private Integer numOfImages = 0;//图片数量
    private String orignalText;//文件详情最原始格式
    private String addtionalText;//文件夹选中文件格式
    private Label textLabel;//文件详情
    private MenuHandler menuHandler;//右键菜单类对象
    private Map<VBox, ImageInDirectory> vBoxImageMap;//VBox对应图片
    private List<ImageInDirectory> selectedImageInDirectories;//选中图片列表
    private List<File> copyList;//复制图片列表


    //获得图片数量
    public void setNumOfImages(Integer numOfImages) {
        this.numOfImages = numOfImages;
    }
    //获得图片大小
    public void setSizeOfImage(Double sizeOfImage) {
        this.sizeOfImage = sizeOfImage;
    }
    //返回复制图片列表
    public List<File> getCopyList() {
        return menuHandler.getCopyList();
    }
    //获得选中图片总大小
    public Integer getSize() {
        return selectSize;
    }
    //构造函数
    public ImageHandler(FlowPane pane, List<File> copyList) {
        flowPane = pane;
        menuHandler = new MenuHandler(flowPane,copyList);
        if(copyList!=null)
        {
            this.copyList = copyList;
        }else copyList = new ArrayList<>();
    }
    public void setTextLabel(Label textLabel,Map<VBox, ImageInDirectory> vBoxImageMap) {
        this.textLabel = textLabel;
        this.orignalText = textLabel.getText();

        this.vBoxImageMap = vBoxImageMap;
        menuHandler.setvBoxImageMap(vBoxImageMap);
    }
    //选中操作
    public void selectImage() {
        // 创建一个列表来跟踪选中的VBox
        ObservableList<Node> selectedVBoxes = FXCollections.observableArrayList();
        // 遍历FlowPane中的所有VBox
        for (Node node : flowPane.getChildren()) {
            if (node instanceof VBox vBox) {
                vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    // 检查是否按下了CTRL键
                    if (event.isControlDown() && event.getButton() == MouseButton.PRIMARY) {
                        // 如果VBox已经被选中，则取消选中
                        if (selectedVBoxes.contains(vBox)) {
                            vBox.setStyle(""); // 清除之前的样式
                            selectedVBoxes.remove(vBox); // 从选中列表中移除
                        } else {
                            vBox.setStyle("-fx-border-color: rgba(126,208,255,0.46);-fx-border-width: 5;");
                            selectedVBoxes.add(vBox);
                        }
                        event.consume();
                    }else if(!event.isControlDown() && event.getButton() == MouseButton.PRIMARY){
                        // 清除其他已选中的VBox的外框
                        for (Node selectedNode : selectedVBoxes) {
                            if (selectedNode instanceof VBox && !selectedNode.equals(vBox)) {
                                ((VBox) selectedNode).setStyle("");
                            }
                        }
                        // 如果当前VBox已经被选中，则取消选中
                        if (selectedVBoxes.contains(vBox)) {
                            vBox.setStyle("");
                            selectedVBoxes.clear();
                        } else {
                            vBox.setStyle("-fx-border-color: rgba(126,208,255,0.46); -fx-border-width: 5;");
                            selectedVBoxes.clear();
                            selectedVBoxes.add(vBox);
                        }
                    }
                    flowPane.addEventHandler(MouseEvent.MOUSE_CLICKED, paneEvent -> {
                        // 检查是否点击了空白区域
                        if (paneEvent.getTarget() instanceof FlowPane) {
                            // 取消所有选中的 VBox
                            for (Node selectedNode : selectedVBoxes) {
                                if (selectedNode instanceof VBox) {
                                    ((VBox) selectedNode).setStyle("");
                                }
                            }
                            selectedVBoxes.clear();
                            textLabel.setText(orignalText + 0);
                            paneEvent.consume();
                        }
                    });
                    if (event.getButton() == MouseButton.SECONDARY) {
                        if (selectedVBoxes.size() == 1) {
                            // 如果只选中了一个 VBox，调用单选右键菜单方法
                            menuHandler.setContextMenu((VBox) selectedVBoxes.get(0)
                                    ,getImageInDirectory(selectedVBoxes));
                        } else if (selectedVBoxes.size() > 1) {
                            // 如果选中了多个 VBox，调用多选右键菜单方法
                            menuHandler.setMultiContextMenu(selectedVBoxes,
                                    getImageInDirectory(selectedVBoxes));
                        }
                    }
                    selectSize = selectedVBoxes.size();
                    textLabel.setText(orignalText + selectSize);
                });
            }
        }
    }
    //获取选中图的对象
    private List<ImageInDirectory> getImageInDirectory(ObservableList<Node> selectedVBoxes) {
        selectedImageInDirectories = new ArrayList<>();

        for (Node node : selectedVBoxes) {
            if (node instanceof VBox) {
                VBox vBox = (VBox) node;
                ImageInDirectory imageInDirectory = vBoxImageMap.get(vBox);
                if (imageInDirectory != null) {
                    selectedImageInDirectories.add(imageInDirectory);
                }
            }
        }
        return selectedImageInDirectories;
    }

    //双击方法图片操作
    public void showImage(){
        for (Node node : flowPane.getChildren()) {
            if (node instanceof VBox vBox) {
                vBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                    if (event.getClickCount()==2) {
                        ImageInDirectory imageInDirectory = vBoxImageMap.get(vBox);
                        try {
                            // 加载另一个FXML文件
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("PDF.fxml"));
                            Parent root = loader.load();
                            SlideController pdfCtrl=loader.getController();
                            File file = new File(imageInDirectory.getImagePath());
                            pdfCtrl.initialize(file.getParentFile(),imageInDirectory);
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(getClass().getResource("shadowStyle.css").toExternalForm());
                            Stage stage = new Stage();
                            stage.setScene(scene);
                            stage.setTitle("幻灯片");
                            //设置程序图标
                            stage.getIcons().add(new Image("picIcon2.png"));
                            // 设置装饰栏样式为无
                            stage.initStyle(StageStyle.UNDECORATED);
                            //窗口拖动
                            addDraggableNode(stage, root);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
    //鼠标偏移量
    private double xOffset = 0;
    private double yOffset = 0;

    private void addDraggableNode(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
    //空白区域操作
    public void blank() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem = new MenuItem("粘贴");
        ImageInDirectory image = vBoxImageMap.get(flowPane.getChildren().get(0));
        menuItem.setOnAction(actionEvent -> {
            try {
                menuHandler.pasteImage(image,copyList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        contextMenu.getItems().add(menuItem);
        flowPane.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getButton() == MouseButton.SECONDARY) { // 检查是否是右键点击事件
                if (event.getTarget() == flowPane) { // 检查是否是空白区域
                    contextMenu.show(flowPane, event.getScreenX(), event.getScreenY());
                } else {
                    contextMenu.hide();
                }
            }
        });
    }
}
