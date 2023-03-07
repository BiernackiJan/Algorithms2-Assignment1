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

        writableImage1 = writableImage;
    }

    @FXML
    private Slider threshholdChange;

    public void imageAdjust(){//TODO: refactor this code to change image to dark background and bright white spots
        defaultImage = image.getImage();
        int height = (int) defaultImage.getHeight();
        int width = (int) defaultImage.getWidth();
        PixelReader pixelReader = defaultImage.getPixelReader();
        PixelWriter pixelWriter = writableImage1.getPixelWriter();
        double threshold = threshholdChange.getValue()/50; // Adjust this threshold as needed

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                if (color.getBrightness() > threshold) {
                    pixelWriter.setColor(x, y, Color.WHITE);
                } else {
                    pixelWriter.setColor(x, y, Color.BLACK);
                }
            }
        }
//        int height = (int) defaultImage.getHeight();
//        int width = (int) defaultImage.getWidth();
//        PixelReader pixelReader = defaultImage.getPixelReader();
//        PixelWriter pixelWriter = writableImage1.getPixelWriter();
//        int value = 0;
//        int value1 = 255;
//
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                Color color = pixelReader.getColor(x, y);
//                int red = (int) (color.getRed() * value);
//                int green = (int) (color.getGreen() * value);
//                int blue = (int) (color.getBlue() * value);
//                Color color1 = Color.rgb(red, green, blue);
//                pixelWriter.setColor(x, y, color1.darker());
//
//                Color color2 = pixelReader.getColor(x, y).grayscale();
//                pixelWriter.setColor(x, y, color2);
//            }
//        }
    }

    public void adjust(ActionEvent actionEvent) {
        imageAdjust();
    }


    public void analyze(ActionEvent actionEvent) {
        int width = (int) writableImage1.getWidth();
        int height = (int) writableImage1.getHeight();

        PixelReader pixelReader = writableImage1.getPixelReader();
        double threshold = threshholdChange.getValue()/50;

        //initialize the UnionAlgorithm
        UnionAlgo u = new UnionAlgo( width * height);


        UnionAlgo disjointSet = new UnionAlgo(height * width);

        // Iterate through each pixel and merge sets for neighboring stars
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);
                int pixelIndex = y * width + x;

                if (color.getBrightness() > threshold) {
                    // Merge with neighboring stars
                    if (x > 0 && pixelReader.getColor(x-1, y).getBrightness() > threshold) {
                        disjointSet.union(pixelIndex, pixelIndex-1);
                    }
                    if (y > 0 && pixelReader.getColor(x, y-1).getBrightness() > threshold) {
                        disjointSet.union(pixelIndex, pixelIndex-width);
                    }
                    if (x < width-1 && pixelReader.getColor(x+1, y).getBrightness() > threshold) {
                        disjointSet.union(pixelIndex, pixelIndex+1);
                    }
                    if (y < height-1 && pixelReader.getColor(x, y+1).getBrightness() > threshold) {
                        disjointSet.union(pixelIndex, pixelIndex+width);
                    }
                }
            }
        }




    }
    /*public static void main(String[] args) { //TODO: refactor this code to analyse the image
        // Initialize the UnionFind data structure.
        UnionFind uf = new UnionFind(width * height);

        // Iterate over each pixel in the image.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check if the pixel is a star.
                if (isStar(image, x, y)) {
                    // Add the pixel to the UnionFind data structure.
                    int index = y * width + x;
                    uf.union(index, index);

                    // Check the neighboring pixels and union them if they are stars.
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;
                            if (isStar(image, nx, ny)) {
                                int nindex = ny * width + nx;
                                uf.union(index, nindex);
                            }
                        }
                    }
                }
            }
        }

        // Get the sets of pixels that are part of each union.
        List<Set<Integer>> sets = uf.getSets();

        // Extract the sub-images corresponding to each union.
        List<WritableImage> subImages = new ArrayList<>();
        for (Set<Integer> set : sets) {
            int xmin = width;
            int xmax = 0;
            int ymin = height;
            int ymax = 0;
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                xmin = Math.min(xmin, x);
                xmax = Math.max(xmax, x);
                ymin = Math.min(ymin, y);
                ymax = Math.max(ymax, y);
            }
            int sw = xmax - xmin + 1;
            int sh = ymax - ymin + 1;
            WritableImage subImage = new WritableImage(sw, sh);
            PixelWriter writer = subImage.getPixelWriter();
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                int sx = x - xmin;
                int sy = y - ymin;
                Color color = image.getPixelReader().getColor(x, y);
                writer.setColor(sx, sy, color);
            }
            subImages.add(subImage);
        }

        // Do something with the sub-images.
        for (WritableImage subImage : subImages) {
            // ...
        }
    }

    public static boolean isStar(WritableImage image, int x, int y) {
        // Check if the pixel at (x, y) is a star.
        // ...
    }

    TODO: This one stores the location of each found star
    public static void main(String[] args) {
        // Load the image from a file or create a new image.
        Image image = new Image("stars.png");

        // Initialize the UnionFind data structure.
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();
        UnionFind uf = new UnionFind(width * height);

        // Iterate over each pixel in the image.
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Check if the pixel is a star.
                if (isStar(image, x, y)) {
                    // Add the pixel to the UnionFind data structure.
                    int index = y * width + x;
                    uf.union(index, index);

                    // Check the neighboring pixels and union them if they are stars.
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue;
                            int nx = x + dx;
                            int ny = y + dy;
                            if (nx < 0 || ny < 0 || nx >= width || ny >= height) continue;
                            if (isStar(image, nx, ny)) {
                                int nindex = ny * width + nx;
                                uf.union(index, nindex);
                            }
                        }
                    }
                }
            }
        }

        // Get the sets of pixels that are part of each union.
        List<Set<Integer>> sets = uf.getSets();

        // Store the locations of each union in the original image.
        List<List<Point2D>> unionLocations = new ArrayList<>();
        for (Set<Integer> set : sets) {
            List<Point2D> locations = new ArrayList<>();
            for (int index : set) {
                int x = index % width;
                int y = index / width;
                locations.add(new Point2D(x, y));
            }
            unionLocations.add(locations);
        }

        // Do something with the union locations.
        for (List<Point2D> locations : unionLocations) {
            // ...
        }
    }

    public static boolean isStar(Image image, int x, int y) {
        // Check if the pixel at (x, y) is a star.
        // ...
    }



    */




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
