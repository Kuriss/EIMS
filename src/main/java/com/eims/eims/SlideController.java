//播放时滚轮放大缩小仍然可用 且播放时放大拖拽可用
package com.eims.eims;

import javafx.animation.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SlideController {
    @FXML
    private ImageView pdfcloseButton;
    @FXML
    private ImageView pdfminButton;

    @FXML
    private ImageView test_image;           //图片;     //添加文件夹按钮;
    @FXML
    private ImageView playNpauseButton;                     //播放暂停按钮;
    private boolean isPlaying = false; // 用于跟踪播放状态，默认为 false
    @FXML
    private ImageView previousPictureButton;            //上一张按钮;
    @FXML
    private ImageView nextPictureButton;                //下一张按钮;
    @FXML
    private ImageView zoomInButton;                     //放大按钮;
    @FXML
    private ImageView zoomOutButton;                    //缩小按钮;
    @FXML
    private Text fileNameText;                          //图片名字信息;
    @FXML
    private ComboBox styleComboBox;                         //幻灯片风格选择框;
    private List<Image> imageList = new ArrayList<>();          // 图片列表
    private int currentIndex = 0;                        // 当前图片索引
    private Timeline timeline;                            // 时间轴用于播放幻灯片
    private FadeTransition fadeTransition;                  //淡入淡出动画;
    private double scaleFactor = 1.0;                   //缩放倍数;
    private double mouseDownX = 0;                      //鼠标操作X坐标
    private double mouseDownY = 0;                      //鼠标操作Y坐标
    private ImageInDirectory image;
    public File selectedDirectory;
    Image pauseImage = new Image(getClass().getResource("/picture/pause-circle-fill.png").toExternalForm());
    Image playImage = new Image(getClass().getResource("/picture/play-arrow-fill.png").toExternalForm());


    public SlideController(){
        System.out.println("构造函数");
    }

    //初始化函数(页面加载时执行)
    @FXML
    void initialize(File selectedDirectory,ImageInDirectory image) {
        //初始化幻灯片风格选择框选项;
        ObservableList<String> observableList = FXCollections.observableArrayList("常规", "淡入淡出", "滑动切换","翻转切换","缩放切换","闪烁切换");
        styleComboBox.setItems(observableList);
        this.selectedDirectory = selectedDirectory;
        this.image = image;
       //添加鼠标按键事件监听器(用于拖拽放大的图片)
        addMouseListeners();
        //添加鼠标滚轮事件监听器(用于滚轮放大缩小图片)
        addScrollListeners();
        handleSelectFolderButtonAction();

    }

    // 最小化窗口
    @FXML
    private void handlepdfMinimizeButtonClick() {
        Stage stage = (Stage) pdfminButton.getScene().getWindow();
        stage.setIconified(true);
    }

    // 关闭窗口
    @FXML
    private void handlepdfCloseButtonClick() {
        Stage stage = (Stage) pdfcloseButton.getScene().getWindow();
        stage.close();
    }

    //播放暂停按钮
    @FXML
    private void handlePlayPauseButtonClick(MouseEvent event) {
        if (isPlaying) {
            // 如果当前是播放状态，则切换为暂停状态
            playNpauseButton.setImage(playImage); // 设置按钮图标为播放图标
            handleStopButtonAction(new ActionEvent()); // 停止播放幻灯片
        } else {
            // 如果当前是暂停状态，则切换为播放状态
            playNpauseButton.setImage(pauseImage); // 设置按钮图标为暂停图标
            handlePlayButtonAction(new ActionEvent()); // 播放幻灯片
        }
        // 切换播放状态
        isPlaying = !isPlaying;
    }


    //选择文件夹
    @FXML
    private void handleSelectFolderButtonAction() {
        // 如果用户选择了文件夹，则读取文件夹中的所有图片文件
        if (selectedDirectory != null) {
            // 清空图片列表
            imageList.clear();
            // 读取文件夹中的所有图片文件
            File[] files = selectedDirectory.listFiles();


            if (files != null) {
                for (File file : files) {
                    if (isImageFile(file)) {
                        Image image = new Image(file.toURI().toString());
                        imageList.add(image);
                    }
                }
            }


            // 显示第一张图片
            if(image==null) {
                if (!imageList.isEmpty()) {
                    test_image.setImage(imageList.get(0));
                    //更新文件夹第一张图片的名字;
                    updateFileNameTextField();
                }
            }else{
                test_image.setImage(image.imageView.getImage());
            }
        }
    }
    // 检查文件是否为图片文件
    private boolean isImageFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif");
    }


    //上一张;
    @FXML
    private void handlePreviousButtonAction(MouseEvent event) {

        // 重置位置
        test_image.setTranslateX(0);
        test_image.setTranslateY(0);

        if (imageList.isEmpty() || imageList.size() <= 1) {
            return; // 如果图片列表为空或只有一张图片，则不做任何操作
        }
        currentIndex = (currentIndex - 1 + imageList.size()) % imageList.size();
        Image previousImage = imageList.get(currentIndex);
        //重置缩放比例;
        test_image.setScaleX(1.0);
        test_image.setScaleY(1.0);
        scaleFactor = 1.0;

        updateImageView(previousImage);
        updateFileNameTextField();
    }

    // 点击“下一张”按钮事件处理方法
    @FXML
    private void handleNextButtonAction(MouseEvent event) {
        // 重置位置
        test_image.setTranslateX(0);
        test_image.setTranslateY(0);

        if (imageList.isEmpty() || imageList.size() <= 1) {
            return; // 如果图片列表为空或只有一张图片，则不做任何操作
        }
        currentIndex = (currentIndex + 1) % imageList.size();
        Image nextImage = imageList.get(currentIndex);
        //重置缩放比例;
        test_image.setScaleX(1.0);
        test_image.setScaleY(1.0);
        scaleFactor = 1.0;

        updateImageView(nextImage);
        updateFileNameTextField();
    }

    //放大图片;
    @FXML
    private void handleZoomInButtonAction(MouseEvent event) {
        if (test_image.getImage() == null) {
            // 如果不存在图片，不做任何操作，或者给用户一个提示
            return;
        }
        // 放大图片
        scaleFactor *= 1.2; // 增加20%的缩放比例
        test_image.setScaleX(scaleFactor);
        test_image.setScaleY(scaleFactor);
    }

    //缩小图片;
    @FXML
    private void handleZoomOutButtonAction(MouseEvent event) {
        if (test_image.getImage() == null) {
            // 如果不存在图片，不做任何操作，或者给用户一个提示
            return;
        }
        // 缩小图片
        scaleFactor /= 1.2; // 减少20%的缩放比例
        test_image.setScaleX(scaleFactor);
        test_image.setScaleY(scaleFactor);
        //若图片已经回到初始大小,重置图片位置;
        if(scaleFactor <= 1.0){
            test_image.setTranslateX(0);
            test_image.setTranslateY(0);
        }
    }

    // 添加鼠标事件的监听器
    private void addMouseListeners() {
        // 添加鼠标按下事件的监听器
        test_image.setOnMousePressed(event -> {
            // 只有在图片被放大时才允许拖拽
            if (scaleFactor > 1.0) {
                // 记录鼠标按下时的坐标
                mouseDownX = event.getSceneX();
                mouseDownY = event.getSceneY();
            }
        });

        // 添加鼠标拖拽事件的监听器
        test_image.setOnMouseDragged(event -> {
            // 只有在图片被放大时才允许拖拽
            if (scaleFactor > 1.0) {
                // 计算鼠标移动的距离
                double deltaX = event.getSceneX() - mouseDownX;
                double deltaY = event.getSceneY() - mouseDownY;

                // 移动图片
                test_image.setTranslateX(test_image.getTranslateX() + deltaX);
                test_image.setTranslateY(test_image.getTranslateY() + deltaY);

                // 更新鼠标按下时的坐标
                mouseDownX = event.getSceneX();
                mouseDownY = event.getSceneY();
            }
        });
    }

    // 添加鼠标滚轮事件的监听器
    private void addScrollListeners() {
        // 添加鼠标滚轮事件的监听器
        test_image.setOnScroll(event -> {
            // 获取滚轮滚动的量
            double deltaY = event.getDeltaY();

            // 根据滚轮滚动的方向放大或缩小图片
            if (deltaY > 0) {
                scaleFactor *= 1.2; // 向上滚动，放大图片
            } else {
                scaleFactor /= 1.2; // 向下滚动，缩小图片
            }

            // 更新图片的缩放比例
            test_image.setScaleX(scaleFactor);
            test_image.setScaleY(scaleFactor);

            //若图片已经回到初始大小,重置图片位置;
            if(scaleFactor <= 1.0){
                test_image.setTranslateX(0);
                test_image.setTranslateY(0);
            }
        });
    }


    //播放幻灯片
    @FXML
    private void handlePlayButtonAction(ActionEvent event){
        //播放幻灯片时禁用按钮;
        previousPictureButton.setDisable(true);
        nextPictureButton.setDisable(true);
        zoomInButton.setDisable(true);
        zoomOutButton.setDisable(true);

        if(imageList.isEmpty())
            return;

        // 如果时间轴已经在运行，则不执行播放操作,以防播放速度叠加;
        if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
            return;
        }
        String selectedStyle = (String)styleComboBox.getValue();
        if (selectedStyle == null || selectedStyle.isEmpty()) {
            playSlideshow(); // 如果没有选择项目，则默认使用常规幻灯片
        } else {
            switch (selectedStyle) {
                case "常规":
                    playSlideshow();
                    break;
                case "淡入淡出":
                    playFadeSlideshow();
                    break;
                case "滑动切换":
                    playSlideSlideshow();
                    break;
                case "翻转切换":
                    playFlipSlideshow();
                    break;
                case "缩放切换":
                    playZoomSlideshow();
                    break;
                case "闪烁切换":
                    playBlinkSlideshow();
                    break;
                case "随机切换":
                    playRandomSlideshow();
                    break;
                default:
                    playSlideshow(); // 默认使用常规幻灯片
                    break;
            }
        }
    }

    // 播放常规幻灯片
    private void playSlideshow() {

        timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {          //参数为两张图片交替时间间隔;
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);
            updateImageView(nextImage);
            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 播放淡入淡出幻灯片
    private void playFadeSlideshow() {

        timeline = new Timeline(new KeyFrame(Duration.seconds(2.3), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);

            fadeTransition = new FadeTransition(Duration.seconds(1), test_image);
            fadeTransition.setFromValue(1.0);                           //参数为图片初始透明度(1.0为完全不透明)
            fadeTransition.setToValue(0.0);                             //参数为图片目标透明度(0.0为完全透明)
            fadeTransition.setOnFinished(event1 -> {
                test_image.setImage(nextImage);
                FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), test_image);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeTransition.play();

            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 播放滑动切换幻灯片
    private void playSlideSlideshow() {

        //创建时间轴,每2.3s执行一次切换;
        timeline = new Timeline(new KeyFrame(Duration.seconds(2.3), e -> {
            //更新图片索引;
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);
            Random random = new Random();

            //调用滑动动画过渡;
            slideTransition(nextImage, random);
            //更新图片文件名;
            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);            //时间轴设置为无限循环;
        timeline.play();                                        //启动时间轴;
    }

    // 滑动切换过渡方法，参数为下一张图片和随机数生成器(随机数用于决定图片滑动方向)
    private void slideTransition(Image nextImage, Random random) {
        //创建过渡动画;
        TranslateTransition slideTransition = new TranslateTransition(Duration.seconds(1), test_image);

        int directionOut = random.nextInt(4);
        int directionIn = random.nextInt(4);

        switch (directionOut) {
            case 0: // 向左滑动
                slideTransition.setFromX(0);
                slideTransition.setToX(-test_image.getFitWidth());
                break;
            case 1: // 向右滑动
                slideTransition.setFromX(0);
                slideTransition.setToX(test_image.getFitWidth());
                break;
            case 2: // 向上滑动
                slideTransition.setFromY(0);
                slideTransition.setToY(-test_image.getFitHeight());
                break;
            case 3: // 向下滑动
                slideTransition.setFromY(0);
                slideTransition.setToY(test_image.getFitHeight());
                break;
        }

        slideTransition.setOnFinished(event1 -> {
            test_image.setImage(nextImage);
            test_image.setTranslateX(0);
            test_image.setTranslateY(0);

            switch (directionIn) {
                case 0: // 向左滑动
                    test_image.setTranslateX(test_image.getFitWidth());
                    break;
                case 1: // 向右滑动
                    test_image.setTranslateX(-test_image.getFitWidth());
                    break;
                case 2: // 向上滑动
                    test_image.setTranslateY(test_image.getFitHeight());
                    break;
                case 3: // 向下滑动
                    test_image.setTranslateY(-test_image.getFitHeight());
                    break;
            }

            TranslateTransition slideInTransition = new TranslateTransition(Duration.seconds(1), test_image);
            slideInTransition.setToX(0);
            slideInTransition.setToY(0);
            slideInTransition.play();
        });

        slideTransition.play();
    }


    // 播放翻转切换幻灯片
    private void playFlipSlideshow() {

        timeline = new Timeline(new KeyFrame(Duration.seconds(2.3), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);

            // 创建翻转动画
            RotateTransition rotateOut = new RotateTransition(Duration.seconds(1), test_image);
            rotateOut.setAxis(Rotate.Y_AXIS); // 沿着 Y 轴翻转
            rotateOut.setFromAngle(0); // 从 0 度开始
            rotateOut.setToAngle(90); // 翻转到 90 度
            rotateOut.setOnFinished(event1 -> {
                test_image.setImage(nextImage); // 更新图片
                RotateTransition rotateIn = new RotateTransition(Duration.seconds(1), test_image);
                rotateIn.setAxis(Rotate.Y_AXIS);
                rotateIn.setFromAngle(-90); // 从 -90 度开始反向翻转
                rotateIn.setToAngle(0); // 翻转到 0 度
                rotateIn.play(); // 播放反向翻转动画
            });
            rotateOut.play(); // 播放翻转动画

            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    //缩放切换
    private void playZoomSlideshow() {

        timeline = new Timeline(new KeyFrame(Duration.seconds(2.3), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);

            // 创建缩小动画对象
            ScaleTransition shrinkTransition = new ScaleTransition(Duration.seconds(1), test_image);
            shrinkTransition.setFromX(1.0); // 起始水平缩放值
            shrinkTransition.setFromY(1.0); // 起始垂直缩放值
            shrinkTransition.setToX(0.01);   // 目标水平缩放值
            shrinkTransition.setToY(0.01);   // 目标垂直缩放值

            // 设置动画完成事件
            shrinkTransition.setOnFinished(event -> {
                // 设置新图片
                test_image.setImage(nextImage);
                // 重置图片的缩放值
                test_image.setScaleX(0.1);
                test_image.setScaleY(0.1);
                // 播放放大动画
                playZoomInAnimation();
            });

            // 播放缩小动画
            shrinkTransition.play();

            // 更新文本框显示文件名
            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 播放放大动画
    private void playZoomInAnimation() {
        // 创建放大动画对象
        ScaleTransition zoomInTransition = new ScaleTransition(Duration.seconds(1), test_image);
        zoomInTransition.setFromX(0.1); // 起始水平缩放值
        zoomInTransition.setFromY(0.1); // 起始垂直缩放值
        zoomInTransition.setToX(1.0);   // 目标水平缩放值
        zoomInTransition.setToY(1.0);   // 目标垂直缩放值

        // 播放放大动画
        zoomInTransition.play();
    }

    // 播放闪烁切换幻灯片
    private void playBlinkSlideshow() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);

            test_image.setImage(nextImage);
            test_image.setOpacity(1.0);

            Timeline blinkTimeline = new Timeline(
                    new KeyFrame(Duration.seconds(0.1), ae -> test_image.setOpacity(0.0)),
                    new KeyFrame(Duration.seconds(0.2), ae -> test_image.setOpacity(1.0))
            );
            blinkTimeline.setCycleCount(3); // 设置闪烁次数
            blinkTimeline.play();

            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    // 播放随机风格幻灯片
    private void playRandomSlideshow() {

        timeline = new Timeline(new KeyFrame(Duration.seconds(2.3), e -> {

//            if (timeline != null && timeline.getStatus() == Timeline.Status.RUNNING) {
//                timeline.stop(); // 停止当前正在运行的时间轴
//            }

            currentIndex = (currentIndex + 1) % imageList.size();
            Image nextImage = imageList.get(currentIndex);

            // 创建一个随机数生成器
            Random random = new Random();

            // 随机选择一个切换效果
            int style = random.nextInt(4); // 生成一个0到3的随机数

            switch (style) {
                case 0:
                    playFadeSlideshow();
                    break;
                case 1:
                    playSlideSlideshow();
                    break;
                case 2:
                    playFlipSlideshow();
                    break;
                case 3:
                    playZoomSlideshow();
                    break;
                default:
                    playSlideshow();
                    break;
            }

            updateFileNameTextField();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }



    //停止播放
    @FXML
    private void handleStopButtonAction(ActionEvent event){
        //停止播放时取消按钮禁用;
        previousPictureButton.setDisable(false);
        nextPictureButton.setDisable(false);
        zoomInButton.setDisable(false);
        zoomOutButton.setDisable(false);

        if(timeline != null){
            timeline.stop();
        }
    }


    // 更新图片
    private void updateImageView(Image image) {
        test_image.setImage(image);
    }

    // 获取当前显示的图片文件名
    private void updateFileNameTextField() {
        Image currentImage = imageList.get(currentIndex);
        String fileName = new File(currentImage.getUrl()).getName();
        //去除文件扩展名
        String fileNameWithoutExtension = removeExtension(fileName);

        fileNameText.setText(fileNameWithoutExtension);
    }

    //去除文件扩展名字
    private String removeExtension(String fileName) {
        // 找到最后一个点号的位置
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            // 如果找不到点号，返回原始文件名
            return fileName;
        } else {
            // 截取文件名（不带扩展名）
            return fileName.substring(0, lastDotIndex);
        }
    }
}