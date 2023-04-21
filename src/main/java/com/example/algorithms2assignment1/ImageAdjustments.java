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
    int[] imagePixels;

    static int[] stars;

    int xArray[];

    int yArray[];

    ArrayList<Integer> sizeList = new ArrayList<>();

    ArrayList<Integer> positions = new ArrayList<>();


    public void segmentImage(Image imageStandard, WritableImage writableImage, WritableImage writableImage2, ImageView imageView, ImageView imageView2, double threshold, int minPix, TreeView treeView, int adjust, int star) {
        // Get the height and width of the image
        int height = (int) imageStandard.getHeight();
        int width = (int) imageStandard.getWidth();

        PixelReader imageReader = imageStandard.getPixelReader();
        PixelWriter imageWriter = writableImage.getPixelWriter();

        PixelWriter pixelWriter = writableImage2.getPixelWriter();


        //set the writeable image to the original image
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                Color color = imageReader.getColor(x, y);
                pixelWriter.setColor(x, y, color);
            }
        }

        //set the imagePixels array size to the size of the image
        imagePixels = new int[width * height];
        xArray = new int[width * height]; //set the xArray array size
        yArray = new int[width * height]; //set the yArray array size

        //convert to black and white
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);
                //if the brightness of a pixel is above the threshold, set it to white, otherwise set it to black
                if (color.getBrightness() > threshold) {
                    imageWriter.setColor(x, y, Color.WHITE);
                    xArray[y * width + x] = x; //if the pixel is white set the x value to the xArray
                    yArray[y * width + x] = y; //if the pixel is white set the y value to the yArray
                } else {
                    imageWriter.setColor(x, y, Color.BLACK);
                    xArray[y * width + x] = -1; //if the pixel is black set the x value to -1
                    yArray[y * width + x] = -1; //if the pixel is black set the y value to -1
                }
            }
        }


        //set the imagePixels array to the values of the xArray and yArray
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);

                //convert to grayscale
                double grayscale = color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114;
                Color whiteBlack = grayscale > threshold ? Color.WHITE : Color.BLACK;

                //if the pixel is black set the value to -1, otherwise set it to the value of the pixel in the PixelArray
                if (whiteBlack == Color.BLACK) {
                    imagePixels[y * width + x] = -1;
                } else {
                    imagePixels[y * width + x] = y * width + x;
                }
            }
        }

        writer = pixelWriter;

        //merge the pixels
        mergeUnionArray(width);
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(imagePixels).get("spotMap");


        //add objects to tree view
        addToTreeView(treeView, spotMap, imageStandard, minPix);

        //if Adjust is 1, color the objects in random colors
        if(adjust == 1){
            colorRandom(imagePixels, minPix, imageWriter, xArray, yArray);
        }
        //if Adjust is 2, color one random object in a random color
        if(adjust == 2){
            colorOne(imagePixels, minPix, imageWriter, xArray, yArray);
        }
        //if Adjust is 3, color the chosen object in a random color
        if(adjust == 3){
            chooseToColor(minPix, imageWriter, xArray, yArray, star);
        }
        //if Adjust is 4, color all object in yellow
        if(adjust == 4){
            colorYellow(imagePixels, minPix, imageWriter, xArray, yArray);
        }

        //set the images to the image views
        imageView.setImage(writableImage);
        imageView2.setImage(writableImage2);

        //draw circles on both black and white image and colored image on next plane view
        drawCircles(spotMap, writableImage, writableImage2, Color.BLUE, minPix);

        //draw numbers on both black and white image and colored images
        drawNumbers(spotMap, imageView, imageView2, minPix);
    }


    public void addToTreeView(TreeView treeView, HashMap<Integer, List<Integer>> spotMap, Image image, int minPix) {
        PixelReader imageReader = image.getPixelReader();

        //List to store the size of each object
        LinkedList list = new LinkedList<>();


        //add objects to tree view
        TreeItem<String> rootItem = new TreeItem<>("Objects ");
        treeView.setRoot(rootItem);

        int itemNum = 1;
        int star = 0;
        stars = new int[spotMap.size()];

        for (int item : spotMap.keySet()) {
            List<Integer> spots = spotMap.get(item);



            if (spots.size() >= minPix) {
                int size = spots.size();
                sizeList.add(size);
                stars[star] = item;

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
                star++;
            }
        }



        // Create an ArrayList to store the positions of the items in the TreeView
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

        // Add the items to the TreeView in the correct order
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


    //
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


    //create a hashmap of the x and y coordinates of each object
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


    //create a sorted hashmap with the size of each object
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


    //merge the x and y coordinates of each object
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




    //create hash maps containing the location and root of each object and its values
    public static HashMap<String, HashMap<Integer, List<Integer>>> createLabelMap(int[] imagePixel) {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();
        HashMap<Integer, List<Integer>> valueMap = new HashMap<>();

        for (int i = 0; i < imagePixel.length; i++) {
            if (imagePixel[i] != -1) {
                int root = UnionAlgo.find(imagePixel, i);
                if (!spotMap.containsKey(root)) {
                    spotMap.put(root, new ArrayList<>());
                    valueMap.put(root, new ArrayList<>());
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




    //create a hashmap of the size of each object
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


    //draw the circles on the image
    public void drawCircles(HashMap<Integer, List<Integer>> spotMap, WritableImage imageStandard, WritableImage image2, Color circleColor, int minPix) {
        PixelWriter imageWriter = imageStandard.getPixelWriter();
        PixelWriter image2Writer = image2.getPixelWriter();

        //create hashmap to see the size of each object
        HashMap<Integer, Integer> sizeMap = createSizeMap();

        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);
            //if the spot is big enough
            if (spots.size() >= minPix) {

                //decide on the radius of the circle depending on the size of the spot
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
                                //draw circles on both images
                                imageWriter.setColor(x, y, circleColor);
                                image2Writer.setColor(x, y, circleColor);
                            } else if (distance < circleRadius - 1 && distance >= circleRadius - 2) {
                                imageWriter.setColor(x, y, Color.BLACK);
                            }
                        }
                    }
                }
                numberOfCircles++;
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



    //draw the numbers on the images
    public void drawNumbers(HashMap<Integer, List<Integer>> spotMap, ImageView imageView, ImageView editedImage, int minPix) {

        //create a list of map entries sorted by size from largest to smallest
        List<Map.Entry<Integer, Integer>> sortedSpotList = sortSpots(minPix);
        //create the hashmap of the x and y coordinates of each spot
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray, imagePixels);

        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);
            //if the spot is big enough
            if (spots.size() >= minPix) {
                int counter = 0;
                //find the number of the spot
                for(int i = 0; i < sortedSpotList.size(); i ++){
                    if(root == stars[i]){
                        counter = i + 1;
                    }
                }
                //retrieve the x and y arrays for the image
                HashMap<Integer, List<Integer>> xMap = xyMap.get("xMap");
                HashMap<Integer, List<Integer>> yMap = xyMap.get("yMap");

                //get the x and y coordinates of the root of the spot
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
                gc.setFill(Color.CYAN);
                gc2.setFill(Color.CYAN);

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



    //Color all objects with a random color in the image
    public static void colorRandom(int[] pixelArray, int minSize, PixelWriter pixelWriter, int [] xArray, int [] yArray) {
        // Create the spot map and XY map
        HashMap<String, HashMap<Integer, List<Integer>>> spotMap = createLabelMap(pixelArray);
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray,pixelArray);

        // Iterate over each spot in the spot map
        for (int root : spotMap.get("valueMap").keySet()) { // For each root in the spot map
            // Check if the spot size is greater than or equal to minSize
            if (spotMap.get("valueMap").get(root).size() >= minSize) {
                for (int i = 0; i < stars.length; i++) {
                    if(root == stars[i]) {
                        // Generate a random color
                        Color randomColor = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));

                        // Get the x and y coordinates for the pixels in the spot
                        List<Integer> xCoords = xyMap.get("xMap").get(root);
                        List<Integer> yCoords = xyMap.get("yMap").get(root);

                        // Set the color of each pixel in the spot to the random color
                        for (int j = 0; j < xCoords.size(); j++) {
                            int x = xCoords.get(j);
                            int y = yCoords.get(j);
                            pixelWriter.setColor(x, y, randomColor);
                        }
                    }
                }
            }
        }
    }


    public static void colorYellow(int[] pixelArray, int minSize, PixelWriter pixelWriter, int [] xArray, int [] yArray) {
        // Create the spot map and XY map
        HashMap<String, HashMap<Integer, List<Integer>>> spotMap = createLabelMap(pixelArray);
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray,pixelArray);

        // Iterate over each spot in the spot map
        for (int root : spotMap.get("valueMap").keySet()) { // For each root in the spot map
            // Check if the spot size is greater than or equal to minSize
            if (spotMap.get("valueMap").get(root).size() >= minSize) {
                for (int i = 0; i < stars.length; i++) {
                    if(root == stars[i]) {
                        // Generate a random color
                        Color color = Color.YELLOW;

                        // Get the x and y coordinates for the pixels in the spot
                        List<Integer> xCoords = xyMap.get("xMap").get(root);
                        List<Integer> yCoords = xyMap.get("yMap").get(root);

                        // Set the color of each pixel in the spot to the random color
                        for (int j = 0; j < xCoords.size(); j++) {
                            int x = xCoords.get(j);
                            int y = yCoords.get(j);
                            pixelWriter.setColor(x, y, color);
                        }
                    }
                }
            }
        }
    }





    public void colorOne(int[] pixelArray, int minSize, PixelWriter pixelWriter, int[] xArray, int[] yArray){
        //Create spot map and XY map
        HashMap<String, HashMap<Integer, List<Integer>>> spotMap = createLabelMap(pixelArray);
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray, pixelArray);

        // Get a list of stars with sizes greater than or equal to minSize
        List<Integer> acceptedSpots = new ArrayList<>();
        for (int root : spotMap.get("valueMap").keySet()) {
            if (spotMap.get("valueMap").get(root).size() > minSize) {
                acceptedSpots.add(root);
            }
        }

        int numStars = 0;
        //Count the number of stars
        for(int i = 0; i < stars.length; i++){
            if(stars[i] != 0){
                numStars++;
            }
        }


        // Choose a random star from the list of large stars
        if (!acceptedSpots.isEmpty()) {
            int randomRoot = stars[(int)(Math.random() * numStars)];

            // Generate a random color
            Color randomColor = Color.rgb((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));

            // Get the x and y coordinates for the pixels in the star
            List<Integer> xCoords = xyMap.get("xMap").get(randomRoot);
            List<Integer> yCoords = xyMap.get("yMap").get(randomRoot);

            // Set the color of each pixel in the star to the random color
            for (int i = 0; i < xCoords.size(); i++) {
                int x = xCoords.get(i);
                int y = yCoords.get(i);
                pixelWriter.setColor(x, y, randomColor);
            }
        }
    }


    public void chooseToColor( int minSize, PixelWriter pixelWriter, int[] xArray, int[] yArray, int index){
        //Create spot map and XY map
        HashMap<String, HashMap<Integer, List<Integer>>> spotMap = createLabelMap(imagePixels);
        HashMap<String, HashMap<Integer, List<Integer>>> xyMap = createXYMap(xArray, yArray, imagePixels);

        // Get a list of stars with sizes greater than or equal to minSize
        List<Integer> acceptedSpots = new ArrayList<>();
        for (int root : spotMap.get("valueMap").keySet()) {
            if (spotMap.get("valueMap").get(root).size() > minSize) {
                acceptedSpots.add(root);
            }
        }

        // Generate a random color
        Color randomColor = Color.rgb((int)(Math.random() * 256), (int)(Math.random() * 256), (int)(Math.random() * 256));


        // Get the x and y coordinates for the pixels in the star
        List<Integer> xCoords = xyMap.get("xMap").get(stars[index]);
        List<Integer> yCoords = xyMap.get("yMap").get(stars[index]);

        // Set the color of each pixel in the star to the random color
        for (int i = 0; i < xCoords.size(); i++) {
            int x = xCoords.get(i);
            int y = yCoords.get(i);
            pixelWriter.setColor(x, y, randomColor);
        }
    }
}
