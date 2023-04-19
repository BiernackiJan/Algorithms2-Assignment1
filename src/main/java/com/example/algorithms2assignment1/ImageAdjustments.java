package com.example.algorithms2assignment1;


import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import resources.UnionAlgo;

import java.util.*;



public class ImageAdjustments {

    int numberOfCircles = 0;

    PixelWriter writer;
    static int[] imagePixels;

    int descOrder[];

    int xArray[];

    int yArray[];

    ArrayList<Integer> sizeList = new ArrayList<>();

    ArrayList<Integer> positions = new ArrayList<>();


    public void segmentImage(Image imageStandard, WritableImage writableImage, WritableImage writableImage2, ImageView imageView, ImageView imageView2, double threshold, int minPix, TreeView treeView, int adjust) {
        // Get the height and width of the image
        int height = (int) imageStandard.getHeight();
        int width = (int) imageStandard.getWidth();

        PixelReader imageReader = imageStandard.getPixelReader();
        PixelWriter imageWriter = writableImage.getPixelWriter();

        PixelWriter pixelWriter = writableImage2.getPixelWriter();


        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color color = imageReader.getColor(x, y);
                pixelWriter.setColor(x, y, color);
            }
        }

        imagePixels = new int[width * height];
        xArray = new int[width * height];
        yArray = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);
                if (color.getBrightness() > threshold) {
                    imageWriter.setColor(x, y, Color.WHITE);
                    xArray[y * width + x] = x;
                    yArray[y * width + x] = y;
                } else {
                    imageWriter.setColor(x, y, Color.BLACK);
                    xArray[y * width + x] = -1;
                    yArray[y * width + x] = -1;
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);

                double grayscale = color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114;
                Color whiteBlack = grayscale > threshold ? Color.WHITE : Color.BLACK;


                if (whiteBlack == Color.BLACK) {
                    imagePixels[y * width + x] = -1;
                } else {
                    imagePixels[y * width + x] = y * width + x;
                }
            }
        }

        writer = pixelWriter;

        mergeUnionArray(width);
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(imagePixels).get("spotMap");
        HashMap<Integer, List<Integer>> valueMap = createLabelMap(imagePixels).get("valueMap");
        HashMap<Integer, Integer> sizeMap = createSizeMap();



        //add objects to tree view
        addToTreeView(treeView, spotMap, imageStandard, minPix);

        if(adjust == 1){
            colorRandom(imagePixels, minPix, imageWriter, xArray, yArray);
        }

        imageView.setImage(writableImage);
        imageView2.setImage(writableImage2);


        //draw circles on both black and white image and colored image on next plane view
        drawCircles(spotMap, writableImage, writableImage2,imageView, imageView2, Color.BLUE, minPix);

//        numberObjects(imageView,imageView2,minPix);
        drawNumbers(spotMap, writableImage, writableImage2, imageView, imageView2, minPix);
    }


    public void addToTreeView(TreeView treeView, HashMap<Integer, List<Integer>> spotMap, Image image, int minPix) {
        PixelReader imageReader = image.getPixelReader();

        LinkedList list = new LinkedList<>();


        TreeItem<String> rootItem = new TreeItem<>("Objects ");
        treeView.setRoot(rootItem);

        int itemNum = 1;

        for (int item : spotMap.keySet()) {
            List<Integer> spots = spotMap.get(item);


            if (spots.size() >= minPix) {
                int size = spots.size();
                sizeList.add(size);


                TreeItem itemItem = new TreeItem<>(itemNum);
                itemItem.getChildren().add(new TreeItem<>("Num Pixels: " + spots.size()));
                float red = 0;
                float green = 0;
                float blue = 0;
                for (int spot : spots) {
                    Color color = imageReader.getColor(spot % (int) image.getWidth(), spot / (int) image.getWidth());
                    red += color.getRed();
                    green += color.getGreen();
                    blue += color.getBlue();
                }
                red /= spots.size();
                green /= spots.size();
                blue /= spots.size();

                itemItem.getChildren().add(new TreeItem<>("Estimated Sulphur: " + red));
                itemItem.getChildren().add(new TreeItem<>("Estimated Hydrogen: " + green));
                itemItem.getChildren().add(new TreeItem<>("Estimated Oxygen: " + blue));

                list.add(itemItem);
            }
        }



        for (int i = 0; i < sizeList.size(); i++) {
            positions.add(i);
        }


        // Sort the ArrayList from largest to smallest
        for (int i = 0; i < sizeList.size() - 1; i++) {
            for (int j = 0; j < sizeList.size() - i - 1; j++) {
                if (sizeList.get(j) < sizeList.get(j + 1)) {

                    // Swap the elements in the sizeList
                    int temp = sizeList.get(j);
                    sizeList.set(j, sizeList.get(j + 1));
                    sizeList.set(j + 1, temp);

                    // Swap the elements in the positions list
                    temp = positions.get(j);
                    positions.set(j, positions.get(j + 1));
                    positions.set(j + 1, temp);
                }
            }
        }

        for (int i = 0; i < positions.size(); i++) {
            rootItem.getChildren().add((TreeItem) list.get(positions.get(i)));
            int j = i + 1;
            rootItem.getChildren().get(i).setValue("Celestial Object " + j);
        }
    }




    //----------------------------------------------------------------------------------------
    //                                       Helper Methods
    //----------------------------------------------------------------------------------------



    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }


    public static int partitioning(int[] roots, int[] sizes, int left, int right) {
        int turn = sizes[right];
        int i = left - 1;
        for (int j = left; j <= right - 1; j++) {
            if (sizes[j] >= turn) {
                i++;
                swap(roots, i, j);
                swap(sizes, i, j);
            }
        }
        swap(roots, i + 1, right);
        swap(sizes, i + 1, right);
        return i + 1;
    }




    public static void fastSort(int[] roots, int[] sizes, int left, int right) {
        if (left < right) {
            int index = partitioning(roots, sizes, left, right);
            fastSort(roots, sizes, left, index - 1);
            fastSort(roots, sizes, index + 1, right);
        }
    }







    //-------------------------------------------------------------------------------------
    //                                  HashMaps
    //-------------------------------------------------------------------------------------


    public static HashMap<String, HashMap<Integer, List<Integer>>> createXYMap(int[] xArray, int[] yArray, int[] imagePixels) {
        HashMap<Integer, List<Integer>> xMap = new HashMap<>();
        HashMap<Integer, List<Integer>> yMap = new HashMap<>();

        for (int i = 0; i < xArray.length; i++) {
            if (xArray[i] != -1) {
                int root = UnionAlgo.find(imagePixels, i);
                if (!xMap.containsKey(root)) {
                    xMap.put(root, new ArrayList<Integer>());
                    yMap.put(root, new ArrayList<Integer>());
                }
                xMap.get(root).add(xArray[i]);
                yMap.get(root).add(yArray[i]);

            }
        }
        HashMap<String, HashMap<Integer, List<Integer>>> resultMap = new HashMap<>();
        resultMap.put("xMap", xMap);
        resultMap.put("yMap", yMap);
        return resultMap;
    }



    public List<Map.Entry<Integer, Integer>> sortSpots(int minSize) {
        HashMap<Integer, Integer> sizeMap = createSizeMap();
        int[] roots = new int[sizeMap.size()];
        int[] sizes = new int[sizeMap.size()];

        int index = 0;
        for (int root : sizeMap.keySet()) {
            roots[index] = root;
            sizes[index] = sizeMap.get(root);
            index++;
        }

        fastSort(roots, sizes, 0, roots.length - 1);

        List<Map.Entry<Integer, Integer>> sortedSpotMap  = new ArrayList<>();
        for (int i = 0; i < roots.length; i++) {
            if (sizes[i] > minSize) {
                int root = roots[i];
                int size = sizes[i];
                sortedSpotMap.add(new AbstractMap.SimpleEntry<>(root, size));
            }
        }
        return sortedSpotMap;
    }



    public void mergeUnionArray( int width) {
        for (int i = 0; i < imagePixels.length; i++) {
            if ( imagePixels[i] != -1) {
                if(i+1 < imagePixels.length && imagePixels[i+1] != -1 && i % width != width - 1)
                    UnionAlgo.union(imagePixels, i, i+1);

                if(i+width < imagePixels.length && imagePixels[i+width] != -1)
                    UnionAlgo.union(imagePixels, i, i+width);
            }
        }
    }





    public static HashMap<String, HashMap<Integer, List<Integer>>> createLabelMap(int[] imagePixel) {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();
        HashMap<Integer, List<Integer>> valueMap = new HashMap<>();

        for (int i = 0; i < imagePixel.length; i++) {
            if (imagePixel[i] != -1) {
                int root = UnionAlgo.find(imagePixel, i);
                if (!spotMap.containsKey(root)) {
                    spotMap.put(root, new ArrayList<Integer>());
                    valueMap.put(root, new ArrayList<Integer>());
                }
                valueMap.get(root).add(imagePixel[i]);
                if (!spotMap.get(root).contains(imagePixel[i])) {
                    spotMap.get(root).add(imagePixel[i]);
                }
            }
        }
        HashMap<String, HashMap<Integer, List<Integer>>> resultMap = new HashMap<>();
        resultMap.put("spotMap", spotMap);
        resultMap.put("valueMap", valueMap);
        return resultMap;
    }





    public HashMap<Integer, Integer> createSizeMap() {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();
        HashMap<Integer, Integer> sizeMap = new HashMap<>();

        for (int i = 0; i < imagePixels.length; i++) {
            if (imagePixels[i] != -1) {
                int root = UnionAlgo.find(imagePixels, i);
                if (!spotMap.containsKey(root)) {
                    spotMap.put(root, new ArrayList<>());
                    sizeMap.put(root, 0);
                }
                spotMap.get(root).add(imagePixels[i]);
                sizeMap.put(root, sizeMap.get(root) + 1);
            }
        }
        return sizeMap;
    }






    //-------------------------------------------------------------------------------
    //                                  Draw Functions
    //-------------------------------------------------------------------------------


    public void drawCircles(HashMap<Integer, List<Integer>> spotMap, WritableImage imageStandard, WritableImage image2,ImageView imageView, ImageView editedImage, Color circleColor, int minPix) {
        PixelWriter imageWriter = imageStandard.getPixelWriter();
        PixelWriter image2Writer = image2.getPixelWriter();

        HashMap<Integer, Integer> sizeMap = createSizeMap();

        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray, imagePixels);

        int counter = 1;
        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);
            //if the spot is big enough
            if (spots.size() >= minPix) {

                //draw the circles
                int circleRadius;
                if (sizeMap.get(root) < 10)
                    circleRadius = 3;
                else if (sizeMap.get(root) < 20)
                    circleRadius = 6;
                else if (sizeMap.get(root) < 30)
                    circleRadius = 8;
                else if (sizeMap.get(root) < 40)
                    circleRadius = 10;
                else if (sizeMap.get(root) < 50)
                    circleRadius = 13;
                else if (sizeMap.get(root) < 60)
                    circleRadius = 16;
                else if (sizeMap.get(root) < 70)
                    circleRadius = 19;
                else if (sizeMap.get(root) < 80)
                    circleRadius = 22;
                else if (sizeMap.get(root) < 90)
                    circleRadius = 25;
                else if (sizeMap.get(root) < 100)
                    circleRadius = 28;
                else
                    circleRadius = 30;


                //Get the center of the spot by finding the average of the x and y coordinates
                int xSum = 15;
                int ySum = 15;
                for (int spot : spots) {
                    double x2 = spot % imageStandard.getWidth();
                    double y2 = spot / imageStandard.getWidth();
                    xSum += x2;
                    ySum += y2;
                }
                int xCenter = xSum / spots.size();
                int yCenter = ySum / spots.size();


                //draw empty circle around the center of the spot with specified radius and color
                for (int x = xCenter - circleRadius; x <= xCenter + circleRadius; x++) {
                    for (int y = yCenter - circleRadius; y < yCenter + circleRadius; y++) {
                        if (x >= 0 && x < imageStandard.getWidth() && y >= 0 && y < imageStandard.getHeight()) {
                            double distance = Math.sqrt((x - xCenter) * (x - xCenter) + (y - yCenter) * (y - yCenter));
                            if (distance >= circleRadius - 1 && distance <= circleRadius) {
                                imageWriter.setColor(x, y, circleColor);
                                image2Writer.setColor(x, y, circleColor);

                            } else if (distance < circleRadius - 1 && distance >= circleRadius - 2) {
                                imageWriter.setColor(x, y, Color.BLACK);
                            }
                        }
                    }
                }
                numberOfCircles++;

                //color not big enough spots black

//            HashMap<Integer, List<Integer>> xMap = xyMap.get("xMap");
//            HashMap<Integer, List<Integer>> yMap = xyMap.get("yMap");
//
//            List<Integer> xValues = xMap.get(root);
//            List<Integer> yValues = yMap.get(root);
//            int lastIndex = xValues.size() - 1;
//            int x1 = xValues.get(lastIndex) - 20;
//            int y1 = yValues.get(lastIndex) + 20;
//
//            // Create a Canvas object with the same dimensions as the ImageView
//            Canvas canvas = new Canvas(imageView.getImage().getWidth(), imageView.getImage().getHeight());
//            Canvas canvas2 = new Canvas(editedImage.getImage().getWidth(), editedImage.getImage().getHeight());
//
//            // Get the GraphicsContext of the Canvas
//            GraphicsContext gc = canvas.getGraphicsContext2D();
//            GraphicsContext gc2 = canvas2.getGraphicsContext2D();
//
//            // Set the font and color for the number
//            gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//            gc2.setFont(Font.font("Arial", FontWeight.BOLD, 24));
//            gc.setFill(Color.GREEN);
//            gc2.setFill(Color.GREEN);
//
//            // Draw the number on the Canvas at the specified x and y coordinates
//            gc.fillText(String.valueOf(counter), x1, y1);
//            gc2.fillText(String.valueOf(counter), x1, y1);
//            counter++;
//
//            // Create an ImageView with the same image as the original ImageView
//            ImageView numberedImage = new ImageView(imageView.getImage());
//            ImageView numberedImage2 = new ImageView(editedImage.getImage());
//
//            // Add the Canvas to a Group and set it as the content of the new ImageView
//            Group group = new Group(numberedImage, canvas);
//            Group group2 = new Group(numberedImage2, canvas2);
//            numberedImage = new ImageView(group.snapshot(null, null));
//            numberedImage2 = new ImageView(group2.snapshot(null, null));
//
//            // Set the new ImageView as the content of the parent Pane
//            imageView.setImage(numberedImage.getImage());
//            editedImage.setImage(numberedImage2.getImage());
//
//
            }

            else{
                for (int spot : spots) {
                    double x = spot % imageStandard.getWidth();
                    double y = spot / imageStandard.getWidth();
                    image2Writer.setColor((int) x, (int) y, Color.BLACK);
                }
            }

        }
    }




    public void drawNumbers(HashMap<Integer, List<Integer>> spotMap, WritableImage imageStandard, WritableImage image2,ImageView imageView, ImageView editedImage, int minPix) {

        List<Map.Entry<Integer, Integer>> sortedSpotList = sortSpots(minPix);

        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray, imagePixels);

        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);
            //if the spot is big enough
            if (spots.size() >= minPix) {
                int counter = 0;
                for(int i = 0; i < sortedSpotList.size(); i ++){
                    Map.Entry<Integer, Integer> j = sortedSpotList.get(i);
                    if(root == j.getKey()){
                        counter = i+1;
                    }
                }

                HashMap<Integer, List<Integer>> xMap = xyMap.get("xMap");
                HashMap<Integer, List<Integer>> yMap = xyMap.get("yMap");

                List<Integer> xValues = xMap.get(root);
                List<Integer> yValues = yMap.get(root);
                int lastIndex = xValues.size() - 1;
                int x1 = xValues.get(lastIndex) - 20;
                int y1 = yValues.get(lastIndex) + 20;

                // Create a Canvas object with the same dimensions as the ImageView
                Canvas canvas = new Canvas(imageView.getImage().getWidth(), imageView.getImage().getHeight());
                Canvas canvas2 = new Canvas(editedImage.getImage().getWidth(), editedImage.getImage().getHeight());

                // Get the GraphicsContext of the Canvas
                GraphicsContext gc = canvas.getGraphicsContext2D();
                GraphicsContext gc2 = canvas2.getGraphicsContext2D();

                // Set the font and color for the number
                gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                gc2.setFont(Font.font("Arial", FontWeight.BOLD, 24));
                gc.setFill(Color.YELLOW);
                gc2.setFill(Color.YELLOW);

                // Draw the number on the Canvas at the specified x and y coordinates
                gc.fillText(String.valueOf(counter), x1, y1);
                gc2.fillText(String.valueOf(counter), x1, y1);

                // Create an ImageView with the same image as the original ImageView
                ImageView numberedImage = new ImageView(imageView.getImage());
                ImageView numberedImage2 = new ImageView(editedImage.getImage());

                // Add the Canvas to a Group and set it as the content of the new ImageView
                Group group = new Group(numberedImage, canvas);
                Group group2 = new Group(numberedImage2, canvas2);
                numberedImage = new ImageView(group.snapshot(null, null));
                numberedImage2 = new ImageView(group2.snapshot(null, null));

                // Set the new ImageView as the content of the parent Pane
                imageView.setImage(numberedImage.getImage());
                editedImage.setImage(numberedImage2.getImage());
            }
        }
    }



    //Color a single random object with a random color in the image
    public static void colorRandom(int[] pixelArray, int minSize, PixelWriter pixelWriter, int [] xArray, int [] yArray) {
        // Create the spot map and XY map
        HashMap<String, HashMap<Integer, List<Integer>>> spotMap = createLabelMap(pixelArray);
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray,pixelArray);

        // Iterate over each spot in the spot map
        for (int root : spotMap.get("valueMap").keySet()) { // For each root in the spot map
            // Check if the spot size is greater than or equal to minSize
            if (spotMap.get("valueMap").get(root).size() >= minSize) {
                // Generate a random color
                Color randomColor = Color.rgb((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));

                // Get the x and y coordinates for the pixels in the spot
                List<Integer> xCoords = xyMap.get("xMap").get(root);
                List<Integer> yCoords = xyMap.get("yMap").get(root);

                // Set the color of each pixel in the spot to the random color
                for (int i = 0; i < xCoords.size(); i++) {
                    int x = xCoords.get(i);
                    int y = yCoords.get(i);
                    pixelWriter.setColor(x, y, randomColor);
                }
            }
        }
    }
}
