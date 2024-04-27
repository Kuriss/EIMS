package com.eims.eims;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class DirectoryTreeController {


    @FXML
    private BorderPane boderPane;
    @FXML
    private Label welcomeText;



    @FXML
    private Pane bottomPane;//底部显示选中图片数的栏


    //目录树的根节点
    private TreeItem<File> mainTreeItem;

    //目录树
    @FXML
    protected TreeView<File> directoryTree;

    //显示图片的部分
    @FXML
    private FlowPane imageFlowPane;

    private int numOfImage;//图片数量

    private double sizeOfImage;



    public static Label numAndSizeLabel = new Label();//文件夹里图片大小和数量标签


    public static Label currentDirectoryLable = new Label();//文件夹标签

    @FXML
    private Pane topMessagePane;

    //imageflowpane的滚动条
    @FXML
    private ScrollPane imageScrollPane;


    //类似于构造函数的一个东西，在这里对directorytree进行初始化设置
    @FXML
    public void initialize() throws IOException {

        File[] items = File.listRoots();//获取文件系统的根目录
        mainTreeItem = new TreeItem<>();//创建目录树的根节点

        for(File item:items){//遍历根目录
            if(item.isDirectory()){//如果是目录
                TreeItem<File> treeItem = new TreeItem<>(item);//创建目录树节点
                mainTreeItem.getChildren().add(treeItem);//将节点添加到根节点
                addItems(treeItem,0);//添加子节点

            }
        }
        // control.directoryTree = new TreeView<>(mainTreeItem);
        directoryTree.setRoot(mainTreeItem);//给目录树设置root根节点
        directoryTree.setShowRoot(false);//不显示根节点，也就是直接显示cde盘
        directoryTree.setStyle("-fx-focus-color: transparent;");//设置选中时的边框，不要太显眼
        //以上是directorytree的初始化

//        imageScrollPane = new ScrollPane();
//        imageScrollPane.setContent(imageFlowPane);
//        imageScrollPane.setFitToWidth(true); // 让 ScrollPane 宽度适应内容

        imageScrollPane.setFitToWidth(true);//让imageFlowPane充斥整个imageScrollPane
        imageScrollPane.setFitToHeight(true);
        imageScrollPane.setStyle("-fx-focus-color: transparent;");//设置选中时的边框，不要太显眼
        //imageScrollPane.setVgrow(imageScrollPane, javafx.scene.layout.Priority.ALWAYS);

        imageFlowPane.setPadding(new Insets(10, 20, 20, 20));//设置边界
       // imageFlowPane.setOrientation(Orientation.HORIZONTAL);
        imageFlowPane.setHgap(30);//每个图片的水平间距
        imageFlowPane.setVgap(30);//每个图片的垂直间距
        imageFlowPane.setStyle("-fx-background-color: rgb(255,255,255)");
        //imageFlowPane.setPrefSize(579.8, 600);








    }


    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to EIMS Application!");
    }



    @FXML       //点击目录树
    void handle(MouseEvent event) {


        TreeItem<File> selectedItem = directoryTree.getSelectionModel().getSelectedItem();//获取点击到的item
        try {
            addItems(selectedItem, 0);//添加子文件
            getPicture(selectedItem);
        } catch (IOException e) {
            e.printStackTrace();
        }


        //setcellfactory是javafx中设置自定义单元格的方法，也就是一个项
        directoryTree.setCellFactory(new Callback<>() {
            //callback参数定义了如何创建树状图节点的单元格
            @Override  //定义了如何创建管理单元格
            public TreeCell<File> call(TreeView<File> param) {

                //返回创建的单元格
                return new TreeCell<>() {
                    @Override
                    protected void updateItem(File item, boolean empty) {

                        if (!empty) { //如果节点不空
                            super.updateItem(item, false);//调用父类的updateitem方法
                            HBox hBox = new HBox();//创建布局
                            Label label = new Label(isListRoots(item));//标签，内容为目录名字
                            this.setGraphic(hBox);//this指的是TreeCell，设置这个节点布局
                            this.setStyle("-fx-border-color: rgb(240,240,240)");//设置颜色
                            hBox.getChildren().add(label);//把label加到hBox面板中

                        } else {

                            this.setGraphic(null);//为空则清空图形
                        }
                    }
                };
            }
        });



    }


    public void addItems(TreeItem<File> in, int flag) throws IOException {
        File[] fileList = null;

        if(in!=null)
        {
            fileList = in.getValue().listFiles();//获取当前目录的子文件
        }
        if (fileList != null)//如果有存在子文件
        {
            if (flag == 0) {
                in.getChildren().remove(0, in.getChildren().size());//移除已经有的子节点，防止重复添加
            }

            if (fileList.length > 0) {
                for (File file : fileList) //遍历所有子文件
                {
                    if (file.isDirectory() & !file.isHidden())//如果是目录而且不是隐藏文件
                    {
                        TreeItem<File> newItem = new TreeItem<>(file);//创建新节点
                        if (flag == 0) {
                            //最多递归两层，因为只要获取到该目录下的子文件夹还有没有文件夹，从而来显示该目录下的子文件夹是否可以展开
                            addItems(newItem, flag + 1);
                        }
                        in.getChildren().add(newItem);//添加新节点到当前节点的子节点中
                    }
                }
            }
        }
    }

    public String isListRoots(File item) {

        File[] rootlist = File.listRoots();// 获取文件系统的根目录列表

        //遍历
        for (File isListRoots : rootlist) {
            // 检查给定文件是否与当前根目录相同
            if (item.toString().equals(isListRoots.toString())) {
                return item.toString();// 如果相同，返回根目录的路径或名称
            }
        }

        // 如果给定文件不是根目录，则返回文件的名称
        return item.getName();
    }



    public void getPicture(TreeItem<File> file){
        topMessagePane.getChildren().clear();
        numOfImage = 0;//让文件夹中的图片数量重置为0
        sizeOfImage = 0;//让文件夹大小清零
        // 清空之前显示的图片
        imageFlowPane.getChildren().clear();

        File[] fileList = file.getValue().listFiles(); // 获取文件夹中的文件列表

        if(fileList.length>0) {
            //遍历
            for (File value : fileList)
            {
                //如果不是文件夹
                if (!value.isDirectory())
                {
                    String fileName = value.getName();
                    String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);//获取后缀
                    // 程序能够显示的图片格式包括:.JPG、.JPEG、.GIF、.PNG、和.BMP。
                    if (suffix.equals("jpg")||suffix.equals("JPG")||suffix.equals("png")||suffix.equals("BMP")
                            ||suffix.equals("GIF")||suffix.equals("JPEG")||suffix.equals("gif"))
                    {
                        numOfImage++;//图片数+1
                        File fileOfImage = new File(value.getAbsolutePath());
                        sizeOfImage +=fileOfImage.length()/1024.0/1024.0;//把图片的大小加上

                        // 创建 ImageInDirectory 对象并添加到 imageFlowPane 中显示
                        ImageInDirectory imageBoxLabel = new ImageInDirectory("File:"+value.getAbsolutePath(),fileName);
                        imageFlowPane.getChildren().add(imageBoxLabel.getImageLabel());
                    }
                }
            }
        }


        sizeOfImage = Math.round(sizeOfImage * 100.0) / 100.0; // 将小数保留两位
        numAndSizeLabel.setText("图片数量："+numOfImage+"      图片总大小："+sizeOfImage+"MB");
        currentDirectoryLable.setText("文件夹："+file.getValue().getName());
        topMessagePane.getChildren().addAll(currentDirectoryLable,numAndSizeLabel);
       // System.out.println(sizeOfImage);

    }

}