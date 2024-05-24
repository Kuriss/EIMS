package com.eims.eims;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ImageInDirectory {
    private String imageName;
    private String imagePath;
    private final Labeled imageLabel = new Label();
    private final VBox vBox = new VBox();
    private final VBox imageVBox = new VBox();

    private final VBox imageLableVBox = new VBox();

    public ImageView imageView;

    public ImageInDirectory(String imagePath,String imageName){
        this.imagePath = imagePath;
        this.imageName = imageName;
        Image image = new Image(imagePath);// // 创建 Image 对象并设置图片路径
        imageView = new ImageView(image);//把image放到imageview中
        imageView.setFitWidth(150);//设置尺寸
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);//保持缩放比例

        // 创建用于显示图片名称的 Label
        Label nameOfImage = new Label();
        nameOfImage.setText(imageName);

        imageVBox.setPrefSize(150,120);//设置imagebox固定大小
        imageVBox.getChildren().addAll(imageView);//设置imagevbox的内部为imageview，也就是放入图片
        imageVBox.setAlignment(Pos.CENTER); // 垂直布局容器居中对齐
        imageVBox.setStyle("-fx-border-color: rgb(200,200,200)");//设置颜色

        imageLableVBox.setPrefSize(150,30);//设置imageLableVBox固定大小
        imageLableVBox.getChildren().addAll(nameOfImage);//设置imagevbox的内部为nameOfImage，也就是放入lable标签显示图片名称
        imageLableVBox.setAlignment(Pos.CENTER); // 垂直布局容器居中对齐
        imageLableVBox.setStyle("-fx-border-color: rgb(100,100,100)");//设置颜色


        // 将分别包含图片视图和文本标签的两个vbox添加到垂直布局容器中
        vBox.setPrefSize(150,150);//设置固定大小
        vBox.getChildren().addAll(imageVBox, imageLableVBox);
        vBox.setAlignment(Pos.CENTER); // 垂直布局容器居中对齐
        vBox.setSpacing(5); // 设置子元素之间的间距
        EventHandler<? super MouseEvent> onVBoxClick = null;
        vBox.setOnMouseClicked(onVBoxClick);
    }



    public Node getImageLabel() {
        return vBox;
    }

    public String getImagePath()
    {
        if(imagePath.startsWith("File:")){
            imagePath = imagePath.replace("File:","");
        }
        return imagePath;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }
}
