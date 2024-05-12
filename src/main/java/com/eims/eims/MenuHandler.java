package com.eims.eims;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class MenuHandler {

    ContextMenu contextMenu;
    private VBox vbox;
    private Map<VBox, ImageInDirectory> vBoxImageMap;
    public void setvBoxImageMap(Map<VBox, ImageInDirectory> vBoxImageMap) {
        this.vBoxImageMap = vBoxImageMap;
    }


    private FlowPane pane;
    private static List<File> copyList;

    public MenuHandler(FlowPane pane,List<File> copyList) {
        this.pane = pane;
        MenuHandler.copyList = Objects.requireNonNullElseGet(copyList, ArrayList::new);
    }
    public List<File> getCopyList()
    {
        return copyList;
    }

    public void setContextMenu(VBox vbox, List<ImageInDirectory> imageInDirectory) {
        //为图片添加事件
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("重命名");
        MenuItem menuItem2 = new MenuItem("复制");
        MenuItem menuItem3 = new MenuItem("删除");
        // 添加菜单项的点击事件处理器
        menuItem1.setOnAction(event -> {
            rename(imageInDirectory);
        });
        menuItem2.setOnAction(event ->
        {
            copyImage(imageInDirectory);
        });
        menuItem3.setOnAction(event -> {
            deleteImage(imageInDirectory);
        });
        contextMenu.getItems().addAll(menuItem1, menuItem2, menuItem3);
        vbox.setOnContextMenuRequested(event -> {
            if (contextMenu.isShowing()) {
                contextMenu.show(vbox, event.getScreenX(), event.getScreenY());
            }
        });
        vbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    contextMenu.show(vbox, mouseEvent.getScreenX(), mouseEvent.getScreenY());
                }
            }
        });
    }

    public void setMultiContextMenu(ObservableList<Node> selectedVBoxes, List<ImageInDirectory> imageInDirectory) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("批量重命名");
        MenuItem menuItem2 = new MenuItem("批量复制");
        MenuItem menuItem3 = new MenuItem("批量删除");
        menuItem1.setOnAction(event -> {
            multiRename(imageInDirectory);
        });
        menuItem2.setOnAction(event -> {
            copyImage(imageInDirectory);
        });
        menuItem3.setOnAction(event -> {
            deleteImage(imageInDirectory);
        });
        contextMenu.getItems().addAll(menuItem1, menuItem2,menuItem3);
        for (Node vBox : selectedVBoxes) {
            vBox.setOnContextMenuRequested(event -> {
                    contextMenu.show(vBox, event.getScreenX(), event.getScreenY());
            });
        }
    }
    //重命名
    private void rename(List<ImageInDirectory> imageInDirectory) {
        System.out.println("重命名");
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("重命名");
        dialog.setHeaderText("请输入新的名称：");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            // 获取原始文件路径
            String oldFilePath = imageInDirectory.get(0).getImagePath();
            System.out.println(oldFilePath);
            // 构建新的文件路径
            String parentPath = new File(oldFilePath).getParent();
            String newFilePath = parentPath + File.separator + newName;
            System.out.println(newFilePath);
            // 执行重命名操作
            File oldFile = new File(oldFilePath);
            File newFile = new File(newFilePath);
            if (oldFile.renameTo(newFile)) {
                // 更新 ImageInDirectory 中的路径信息
                imageInDirectory.get(0).setImagePath(newFilePath);

            } else {
                System.out.println("重命名失败");
            }
        });
    }
    //复制
    public List<File> copyImage(List<ImageInDirectory> imageInDirectory) {
        System.out.println("复制");
        if(copyList != null){
            copyList.clear();
        }
        for (ImageInDirectory image : imageInDirectory) {
            String oldFilePath = image.getImagePath();
            copyList.add(new File(oldFilePath));
        }
        return copyList;
    }
    //删除
    private void deleteImage(List<ImageInDirectory> imageInDirectory) {
        System.out.println("删除");
        for (ImageInDirectory image : imageInDirectory) {
            String oldFilePath = image.getImagePath();
            File file = new File(oldFilePath);
            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("已删除文件: " + image.getImagePath());
                } else {
                    System.out.println("无法删除文件: " + image.getImagePath());
                }
            } else {
                System.out.println("文件不存在: " + image.getImagePath());
            }
        }
    }
    //粘贴
    public void pasteImage(ImageInDirectory image,List<File> copyList) throws IOException {
        System.out.println("粘贴");
        String parentPath = new File(image.getImagePath()).getParent();
        for(File f : copyList)
        {
            String imagePath=parentPath+File.separator+f.getName();
            File curFile = new File(imagePath);
            String newFileName =curFile.getName();

            String newFilePath = parentPath + File.separator + newFileName;
            boolean pathExists =false;
            if(null==vBoxImageMap) System.out.println("Empty");
            for(Map.Entry<VBox,ImageInDirectory> entry : vBoxImageMap.entrySet())
            {
                ImageInDirectory image1 = entry.getValue();
                String oldFilePath = image1.getImagePath();
                if(newFilePath.equals(oldFilePath)){
                    pathExists = true;
                    break;
                }
            }
            if(!pathExists){
                File targetFile = new File(parentPath, f.getName());
                Files.copy(f.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        copyList.clear();
    }
    //批量重命名
    private void multiRename(List<ImageInDirectory> imageInDirectory) {
        System.out.println("批量重命名");
        TextInputDialog prefixDialog = new TextInputDialog();
        prefixDialog.setTitle("输入名称前缀");
        prefixDialog.setHeaderText("请输入名称前缀：");
        Optional<String> prefixResult = prefixDialog.showAndWait();

        if (!prefixResult.isPresent()) {
            return; // 如果用户取消了输入名称前缀，则退出方法
        }

        String prefix = prefixResult.get();

        TextInputDialog startNumberDialog = new TextInputDialog();
        startNumberDialog.setTitle("输入起始编号");
        startNumberDialog.setHeaderText("请输入起始编号：");
        Optional<String> startNumberResult = startNumberDialog.showAndWait();

        if (!startNumberResult.isPresent()) {
            return; // 如果用户取消了输入起始编号，则退出方法
        }

        int startNumber;
        try {
            startNumber = Integer.parseInt(startNumberResult.get());
        } catch (NumberFormatException e) {
            System.out.println("起始编号格式不正确");
            return;
        }

        TextInputDialog digitNumberDialog = new TextInputDialog();
        digitNumberDialog.setTitle("输入编号位数");
        digitNumberDialog.setHeaderText("请输入编号位数：");
        Optional<String> digitNumberResult = digitNumberDialog.showAndWait();

        if (!digitNumberResult.isPresent()) {
            return; // 如果用户取消了输入编号位数，则退出方法
        }

        int digitNumber;
        try {
            digitNumber = Integer.parseInt(digitNumberResult.get());
        } catch (NumberFormatException e) {
            System.out.println("编号位数格式不正确");
            return;
        }

        int counter = startNumber;
        String format = "%0" + digitNumber + "d"; // 根据编号位数生成格式化字符串

        for (ImageInDirectory image : imageInDirectory) {
            String oldFilePath = image.getImagePath();

            String extension = oldFilePath.substring(oldFilePath.lastIndexOf(".")); // 获取文件扩展名

            String newFileName = prefix + String.format(format, counter) + extension;
            String parentPath = new File(oldFilePath).getParent();
            String newFilePath = parentPath + File.separator + newFileName;

            File oldFile = new File(oldFilePath);
            File newFile = new File(newFilePath);

            if (oldFile.renameTo(newFile)) {
                image.setImagePath(newFilePath);
                System.out.println("重命名成功: " + newFilePath);
            } else {
                System.out.println("重命名失败: " + oldFilePath);
            }

            counter++;
        }
    }

}
