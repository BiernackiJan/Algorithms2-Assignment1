package com.example.algorithms2assignment1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

public class HelloController {
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


    private Image defaultImage;
    private WritableImage writableImage1;
    @FXML
    ImageView image;

    @FXML
    ImageView editedImage;
    public void fileChooser(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(null);
        Image image1 = new Image(String.valueOf(file));
        image.setImage(image1);

        int height=(int)image1.getHeight();
        int width=(int)image1.getWidth();
        PixelReader pixelReader=image1.getPixelReader();
        WritableImage writableImage = new WritableImage(width,height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++){
            for (int x = 0; x < width; x++){
                Color color = pixelReader.getColor(x, y);
                pixelWriter.setColor(x, y, color);
            }
        }

        editedImage.setImage(writableImage);
        defaultImage = image1;
        writableImage1 = writableImage;

    }




    @FXML
    public void imageChoose(ActionEvent event){
        if(imageOption.getSelectionModel().getSelectedIndex()==0){
            fileChooser();
        }
    }
    @FXML
    private ComboBox<String> imageOption;



//    public void redImage (ActionEvent event) {
//
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int green = (int) (color.getGreen() * 255);
//                int blue = (int) (color.getBlue() * 255);
//                Color color2 = Color.rgb(255, green, blue);
//
//
//                pixelWriter.setColor(x, y, color2);
//
//            }
//        }
//
//    }
//
//    public void greenImage(ActionEvent actionEvent){
//
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int red = (int) (color.getRed() * 255);
//                int blue = (int) (color.getBlue() * 255);
//                Color color1 = Color.rgb(red, 255, blue);
//
//
//                pixelWriter.setColor(x, y, color1);
//
//            }
//        }
//    }
//
//    public void blueImage(ActionEvent actionEvent){
//
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int red = (int) (color.getRed() * 255);
//                int green = (int) (color.getGreen() * 255);
//                Color color1 = Color.rgb(red, green, 255);
//
//
//                pixelWriter.setColor(x, y, color1);
//
//            }
//        }
//    }
//
//    public void grayImage(ActionEvent event){
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y).grayscale();
//
//
//                pixelWriter.setColor(x, y, color);
//
//            }
//        }
//    }
//
//
//    @FXML
//    private Slider brightness;
//    @FXML
//    private Slider saturation;
//
//    public void imageBrightness() {
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//        int value = (int) brightness.getValue();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int red = (int) (color.getRed() * value);
//                int green = (int) (color.getGreen() * value);
//                int blue = (int) (color.getBlue() * value);
//                Color color1 = Color.rgb(red, green, blue);
//
//                pixelWriter.setColor(x, y, color1.brighter());
//            }
//        }
//
//    }
//
//    public void imageSaturation(){
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//        int value = (int) saturation.getValue();
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int red = (int) (color.getRed() * value);
//                int green = (int) (color.getGreen() * value);
//                int blue = (int) (color.getBlue() * value);
//                Color color1 = Color.rgb(red, green, blue);
//
//                pixelWriter.setColor(x, y, color1.saturate());
//            }
//        }
//    }






    public void reset(){
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        PixelReader pixelReader = defaultImage.getPixelReader();
        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//        brightness.setValue(127.5);
//        saturation.setValue(127.5);

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
