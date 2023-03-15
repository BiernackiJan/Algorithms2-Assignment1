package com.example.algorithms2assignment1;


import javafx.scene.image.*;
import javafx.scene.paint.Color;
import resources.UnionAlgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdjustments {


    int imagePixels[];
    public void segmentImage(Image imageStandard, ImageView imageView1) {
        // Get the height and width of the image
        int height = (int) imageStandard.getHeight();
        int width = (int) imageStandard.getWidth();

        // Create a PixelReader object to read the pixel values of the imageStandard
        PixelReader pixelReader = imageStandard.getPixelReader();

        // Create a new WritableImage object with the same height and width as the imageStandard
        WritableImage labelImage = new WritableImage(width, height);

        // Create a PixelWriter object to write label values to the new WritableImage
        PixelWriter pixelWriter = labelImage.getPixelWriter();
         // This value can be adjusted to control the threshold

        imagePixels = new int[ width * height];                                                          // Array to store the pixel values of the image (used for union-find)

        for (int y = 0; y < height; y++) { //row
            for (int x = 0; x < width; x++) { //column
                // Get the color of the current pixel
                Color color = pixelReader.getColor(x, y);

                if (color == Color.BLACK) {
                    // This pixel belongs to the background
                    imagePixels[y * width + x] = -1;                                                     // Set the pixel value to -1 (background)
                } else {
                    imagePixels[y * width + x] = y* width + x;                                           // Set the pixel value to the index of the pixel in the array (foreground)
                }
            }
        }

        mergeUnionArray(width);
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(width).get("spotMap");
        HashMap<Integer, List<Integer>> valueMap = createLabelMap(width).get("valueMap");
        HashMap<Integer, Integer> sizeMap = createSizeMap(width);

        //drawCirclesAroundSpots(spotMap, labelImage, Color.BLUE, 20);

//        countSpots(spotMap);
//        System.out.println("Number of spots: " + countSpots(spotMap));
//        System.out.println("Label Map:");
        System.out.println(spotMap);
        System.out.println("Value Set Map:");
//        System.out.println(valueMap);
//        System.out.println("Size of the spots:");
//        System.out.println(sizeMap);
//        sortSpotsBySize(width,minSize);
        printUnionArray(width);

        // Set the new image to the ImageView
        imageView1.setImage(labelImage);


    }



    public void mergeUnionArray( int width) {
        for (int i = 0; i < imagePixels.length; i++) {                                           //for each pixel in the image
            if ( imagePixels[i] != -1) {                                                         //if pixel is not background
                if(i+1 < imagePixels.length && imagePixels[i+1] != -1 && i % width != width - 1)  //if pixel is not on the right edge of the image
                    UnionAlgo.union(imagePixels, i, i+1);                                      //merge with pixel to the right

                if(i+width < imagePixels.length && imagePixels[i+width] != -1)                    //if pixel is not on the bottom edge of the image
                    UnionAlgo.union(imagePixels, i, i+width);                                 //merge with pixel below
            }
        }
    }



    public HashMap<String, HashMap<Integer, List<Integer>>> createLabelMap(int width) {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();           // Create a new HashMap to store a list of values for each root
        HashMap<Integer, List<Integer>> valueMap = new HashMap<>();          // Create a new HashMap to store a set of values for each root

        for (int i = 0; i < imagePixels.length; i++) {                        // For each pixel in the image
            if (imagePixels[i] != -1) {                                       // If the pixel is not background
                int root = UnionAlgo.find(imagePixels, i);                    // Get the root of the current spot
                //int size = unionFind.size(pixelArray, i);                    // Get the size of the current spot
                if (!spotMap.containsKey(root)) {                            // If the root is not already in the HashMap, create a new List and Set for it
                    spotMap.put(root, new ArrayList<Integer>());             // Create a new List for the root
                    valueMap.put(root, new ArrayList<Integer>());
                    // Create a new Set for the root
                }
                valueMap.get(root).add(imagePixels[i]);                       // Add the value to the Set for the root
                if (!spotMap.get(root).contains(imagePixels[i])) {            // If the List for the root does not already contain the value
                    spotMap.get(root).add(imagePixels[i]);                    // Add the value to the List for the root
                }
            }
        }
        HashMap<String, HashMap<Integer, List<Integer>>> resultMap = new HashMap<>();   // Create a new HashMap to store both spotMap and valueMap
        resultMap.put("spotMap", spotMap);                                              // Put spotMap in resultMap with key "spotMap"
        resultMap.put("valueMap", valueMap);                                            // Put valueMap in resultMap with key "valueMap"
        return resultMap;
    }

    public HashMap<Integer, Integer> createSizeMap(int width) {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();           // Create a new HashMap to store a list of values for each root
        //HashMap<Integer, List<Integer>> valueMap = new HashMap<>();          // Create a new HashMap to store a set of values for each root
        HashMap<Integer, Integer> sizeMap = new HashMap<>();                 // Create a new HashMap to store the size of each spot

        for (int i = 0; i < imagePixels.length; i++) {                        // For each pixel in the image
            if (imagePixels[i] != -1) {                                       // If the pixel is not background
                int root = UnionAlgo.find(imagePixels, i);                    // Get the root of the current spot
                if (!spotMap.containsKey(root)) {                            // If the root is not already in the HashMap, create a new List and Set for it
                    spotMap.put(root, new ArrayList<Integer>());             // Create a new List for the root
                    sizeMap.put(root, 0);
                }
                spotMap.get(root).add(imagePixels[i]);                        // Add the value to the Set for the root
                sizeMap.put(root, sizeMap.get(root) + 1);                    // Increment the size of the spot
            }
        }
        return sizeMap;
    }

    public void printUnionArray(int width) {
        for (int i = 0; i < imagePixels.length; i++) {   //for each pixel in the image
            if (i % width == 0) {                       //if pixel is on the left edge of the image
                System.out.println();                   //print a new line
            }
            System.out.print(imagePixels[i] + " ");      //print the pixel value
        }
    }
}
