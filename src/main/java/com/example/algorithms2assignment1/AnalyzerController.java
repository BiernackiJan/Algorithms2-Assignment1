package com.example.algorithms2assignment1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class AnalyzerController {
    @FXML
    private Button btnOverview;
    @FXML
    private Button btnSearch;
    @FXML
    private Button btnSignout;

    @FXML
    private Pane pnlOverview;
    @FXML
    private Pane pnlSearch;

    @FXML
    public void handleClicks(ActionEvent actionEvent) throws IOException {
        if (actionEvent.getSource() == btnOverview) {
            pnlOverview.setStyle("-fx-background-color : #02030A");
            pnlOverview.toFront();
            pnlSearch.setVisible(false);
            pnlOverview.setVisible(true);
        }
        if(actionEvent.getSource()== btnSearch){
            pnlSearch.setStyle("-fx-background-color : #02030A");
            pnlSearch.toFront();
            pnlOverview.setVisible(false);
            pnlSearch.setVisible(true);
        }
        if(actionEvent.getSource()== btnSignout){
            Platform.exit();;
        }
    }


    //original Image
    private Image defaultImage;
    //image next to original image
    private WritableImage writableImage1;

    //Original colored imageView
    @FXML
    ImageView image;

    //Image next to original imageView
    @FXML
    ImageView editedImage;
    public void fileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
//        File file = fileChooser.showOpenDialog(null);
        //laptop
        File file = new File("C:\\Users\\janbi\\OneDrive - South East Technological University (Waterford Campus)\\Wit\\Semester 4\\Data Bases & Algorithms 2\\stars.jpg");
        //PC
//        File file = new File("C:\\Users\\Jan\\OneDrive - South East Technological University (Waterford Campus)\\Wit\\Semester 4\\Data Bases & Algorithms 2\\stars.jpg");

        Image image1 = new Image(String.valueOf(file));
        image.setImage(image1);

        int height=(int)image1.getHeight();
        int width=(int)image1.getWidth();
        PixelReader pixelReader = image1.getPixelReader();
        WritableImage writableImage = new WritableImage(width,height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x, y, color);
            }
        }
        //set the imageViews to the loaded in image
        defaultImage = image1;
        writableImage1 = writableImage;
        editedImage.setImage(writableImage1);
    }



    @FXML
    private TextField minPixels;
    @FXML
    private Slider threshholdChange;
    @FXML
    private TextField numStars;


    public void imageAdjust(){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();

        WritableImage writableImage2 = new WritableImage(width, height);


        ImageAdjustments adjustments = new ImageAdjustments();
        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,0,0);

        numStars.setText(String.valueOf(adjustments.numberOfCircles));

        for(int i = 0; i < adjustments.numberOfCircles; i++){
            chooseStar.getItems().add(i+1);
        }
    }

    public void adjust(ActionEvent actionEvent) {
        imageAdjust();
    }



    @FXML
    ImageView circledImage;

    @FXML
    private ComboBox<String> imageOption;
    @FXML
    public void imageChoose(ActionEvent event){
        if(imageOption.getSelectionModel().getSelectedIndex()==0){
            fileChooser();
        }
    }



    public void colorRand(ActionEvent event){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        ImageAdjustments adjustments = new ImageAdjustments();
        WritableImage writableImage2 = new WritableImage(width, height);

        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,1,0);
    }


    public void highlightAll(ActionEvent event){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        ImageAdjustments adjustments = new ImageAdjustments();
        WritableImage writableImage2 = new WritableImage(width, height);

        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,4,0);
    }



    public void colorOne(ActionEvent event){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        ImageAdjustments adjustments = new ImageAdjustments();
        WritableImage writableImage2 = new WritableImage(width, height);

        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,2, 0);
    }


    @FXML
    private ComboBox<Integer> chooseStar;

//    public void chosenStar(ActionEvent event){
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        ImageAdjustments adjustments = new ImageAdjustments();
//        WritableImage writableImage2 = new WritableImage(width, height);
//
//        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,0, chooseStar.getSelectionModel().getSelectedIndex());
//    }

    public void colorChosen(ActionEvent event){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        ImageAdjustments adjustments = new ImageAdjustments();
        WritableImage writableImage2 = new WritableImage(width, height);

        adjustments.segmentImage(defaultImage, writableImage1,  writableImage2, editedImage, circledImage, threshholdChange.getValue() / 50, Integer.parseInt(minPixels.getText()),objectList,3, chooseStar.getSelectionModel().getSelectedIndex());
    }






    //--------------------------------------------------------------------------------------
    //                                       Pane 2
    //--------------------------------------------------------------------------------------


    @FXML
    private TreeView<String> objectList;

    @FXML
    private void expandAllNodes(ActionEvent event) {
        expandTreeView(objectList.getRoot());
    }

    private void expandTreeView(TreeItem<String> item) {
        if (item != null && !item.isLeaf()) {
            item.setExpanded(true);
            for (TreeItem<String> child : item.getChildren()) {
                expandTreeView(child);
            }
        }
    }









    public void reset(){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        numStars.clear();
        PixelReader pixelReader = defaultImage.getPixelReader();
        PixelWriter pixelWriter = writableImage1.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                int red = (int) (color.getRed() * 255);
                int green = (int) (color.getGreen() * 255);
                int blue = (int) (color.getBlue() * 255);
                Color color1 = Color.rgb(red, green, blue);

                pixelWriter.setColor(x, y, color1);
            }
        }
    }

    @FXML
    public void initialize(){
        imageOption.getItems().addAll("Open");
    }


}
