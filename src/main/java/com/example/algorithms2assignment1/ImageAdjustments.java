package com.example.algorithms2assignment1;


import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import resources.UnionAlgo;

import java.util.*;

public class ImageAdjustments {

    int numberOfCircles = 0;
    int imagePixels[];

    ArrayList<Integer> sizeList = new ArrayList<>();

    ArrayList<Integer> positions = new ArrayList<>();


    public void segmentImage(Image imageStandard, WritableImage writableImage, WritableImage writableImage2, ImageView imageView, ImageView imageView2, double threshold, int minPix, TreeView treeView) {
        // Get the height and width of the image
        int height = (int) imageStandard.getHeight();
        int width = (int) imageStandard.getWidth();

        PixelReader imageReader = imageStandard.getPixelReader();
        PixelWriter imageWriter = writableImage.getPixelWriter();

        imagePixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = imageReader.getColor(x, y);
                if (color.getBrightness() > threshold) {
                    imageWriter.setColor(x, y, Color.WHITE);
                } else {
                    imageWriter.setColor(x, y, Color.BLACK);
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

        mergeUnionArray(width);
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(imagePixels).get("spotMap");
        HashMap<Integer, List<Integer>> valueMap = createLabelMap(imagePixels).get("valueMap");
        HashMap<Integer, Integer> sizeMap = createSizeMap();

        //draw circles on both black and white image and colored image on next plane view
        drawCircles(spotMap, writableImage, writableImage2, Color.BLUE, minPix);


        //add objects to tree view
        addToTreeView(treeView, spotMap, imageStandard, minPix);


        imageView.setImage(writableImage);
        imageView2.setImage(writableImage2);
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



    //Color a single random object with a random color in the image
    public void colorRandomObject(ImageView imageView, WritableImage writableImage) {
        int width = (int) writableImage.getWidth();
        HashMap<Integer, List<Integer>> spotMap = createLabelMap(imagePixels).get("spotMap");

        PixelWriter imageWriter = writableImage.getPixelWriter();
        Random rand = new Random();
        int random = rand.nextInt(spotMap.size());
        int i = 0;
        for (int item : spotMap.keySet()) {
            if (i == random) {
                List<Integer> spots = spotMap.get(item);
                for (int spot : spots) {
                    imageWriter.setColor(spot % width, spot / width, Color.color(Math.random(), Math.random(), Math.random()));
                }
            }
            i++;
        }



        imageView.setImage(writableImage);
    }


    public HashMap<String, HashMap<Integer, List<Integer>>> createLabelMap(int[] imagePixel) {
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



    public void drawCircles(HashMap<Integer, List<Integer>> spotMap, WritableImage imageStandard, WritableImage image2, Color circleColor, int minPix) {
        PixelWriter imageWriter = imageStandard.getPixelWriter();
        PixelWriter image2Writer = image2.getPixelWriter();

        HashMap<Integer, Integer> sizeMap = createSizeMap();

        //iterate through the spotMap
        for (int root : spotMap.keySet()) {
            //get the list of pixels for each spot
            List<Integer> spots = spotMap.get(root);
            //if the spot is big enough
            if (spots.size() >= minPix) {

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
                                image2Writer.setColor(x, y, circleColor);

                            } else if (distance < circleRadius - 1 && distance >= circleRadius - 2) {
                                imageWriter.setColor(x, y, Color.BLACK);
                            }
                        }
                    }
                }
                numberOfCircles++;
            }
            //color not big enough spots black
            else {
                for (int spot : spots) {
                    double x = spot % imageStandard.getWidth();
                    double y = spot / imageStandard.getWidth();
                    image2Writer.setColor((int) x, (int) y,Color.BLACK);
                }
            }
        }
    }
}
