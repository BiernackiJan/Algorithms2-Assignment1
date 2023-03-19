package com.example.algorithms2assignment1;


import javafx.scene.image.*;
import javafx.scene.paint.Color;
import resources.UnionAlgo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageAdjustments {


    int imagePixels[];
    public void segmentImage(Image imageStandard, ImageView imageView, double threshold) {
        // Get the height and width of the image
        int height = (int) imageStandard.getHeight();
        int width = (int) imageStandard.getWidth();

        PixelReader imageReader = imageStandard.getPixelReader();

        WritableImage writableImage = new WritableImage(width, height);

        imagePixels = new int[ width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);

                double grayscale = color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114;
                Color whiteBlack = grayscale > threshold ? Color.WHITE : Color.BLACK;


                if (whiteBlack == Color.BLACK) {
                    imagePixels[y * width + x] = -1;
                } else {
                    imagePixels[y * width + x] = y* width + x;
                }
            }
        }

        mergeUnionArray(width);
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(width).get("spotMap");
        HashMap<Integer, List<Integer>> valueMap = createLabelMap(width).get("valueMap");
        HashMap<Integer, Integer> sizeMap = createSizeMap();


        drawCircles(spotMap, writableImage , Color.BLUE);
//        System.out.println(spotMap);
        //System.out.println("Value Set Map:");
//        System.out.println(valueMap);
        System.out.println(sizeMap);

        //printUnionArray(width);
        imageView.setImage(writableImage);
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



    public HashMap<String, HashMap<Integer, List<Integer>>> createLabelMap(int width) {
        HashMap<Integer, List<Integer>> spotMap = new HashMap<>();
        HashMap<Integer, List<Integer>> valueMap = new HashMap<>();

        for (int i = 0; i < imagePixels.length; i++) {
            if (imagePixels[i] != -1) {
                int root = UnionAlgo.find(imagePixels, i);
                if (!spotMap.containsKey(root)) {
                    spotMap.put(root, new ArrayList<Integer>());
                    valueMap.put(root, new ArrayList<Integer>());
                }
                valueMap.get(root).add(imagePixels[i]);
                if (!spotMap.get(root).contains(imagePixels[i])) {
                    spotMap.get(root).add(imagePixels[i]);
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
        HashMap<Integer, List<Integer>> valueMap = new HashMap<>();
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


    public void printUnionArray(int width) {
        for (int i = 0; i < imagePixels.length; i++) {
            if (i % width == 0) {
                System.out.println();
            }
            System.out.print(imagePixels[i] + " ");
        }
    }


    public void drawCircles(HashMap<Integer, List<Integer>> spotMap, WritableImage imageStandard, Color circleColor) {
        PixelWriter imageWriter = imageStandard.getPixelWriter();

        HashMap<Integer, Integer> sizeMap = createSizeMap();

        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //TODO: make a varying radius depending on the size of the spot
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

            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);

            //Get the center of the spot by finding the average of the x and y coordinates
            int xSum = 0;
            int ySum = 0;
            for (int spot : spots) {
                double x = spot % imageStandard.getWidth();
                double y = spot / imageStandard.getWidth();
                xSum += x;
                ySum += y;
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
                        } else if (distance < circleRadius - 1 && distance >= circleRadius - 2) {
                            Color translucentColor = circleColor.deriveColor(0, 1, 1, 0.0);
                            imageWriter.setColor(x, y, translucentColor);
                        }
                    }
                }
            }
        }
    }
}
