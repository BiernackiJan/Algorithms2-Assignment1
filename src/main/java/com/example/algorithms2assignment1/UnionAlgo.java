package com.example.algorithms2assignment1;

import javafx.scene.paint.Color;
import static java.lang.Math.random;

public class UnionAlgo {
//    private int[] parent;
//    private int[] rank;
//    private Color[] colors;
//    public UnionAlgo(int size) {
//        // Initialize the parent and rank arrays.
//        parent = new int[size];
//        rank = new int[size];
//        colors = new Color[size];
//
//        // Set each element's parent to itself and rank to 0.
//        for (int i = 0; i < size; i++) {
//            parent[i] = i;
//            colors[i] = new Color((float) (random()), (float)(random()), (float)(random()),1);
//        }
//    }
//
//    public int find(int x) {
//        // If x is not the root node, path compress to the root.
//        if (parent[x] != x) {
//            parent[x] = find(parent[x]);
//        }
//        // Return the root node of the set containing x.
//        return parent[x];
//    }
//
//
//
//    public void union(int x, int y) {
//        // Find the root nodes of the sets containing x and y.
//        int rootX = find(x);
//        int rootY = find(y);
//
//        // If the sets are not already merged, merge them.
//        if (rootX != rootY) {
//            // Union by rank to keep the tree balanced.
//            if (rank[rootX] < rank[rootY]) {
//                parent[rootX] = rootY;
//                colors[rootX] = colors[rootY];
//            } else if (rank[rootX] > rank[rootY]) {
//                parent[rootY] = rootX;
//                colors[rootY] = colors[rootX];
//            } else {
//                parent[rootY] = rootX;
//                rank[rootX]++;
//                colors[rootY] = colors[rootX];
//            }
//        }
//    }
//
//    public void setColor(int p, Color color) {
//        int rootP = find(p);
//        colors[rootP] = color;
//    }
private int[] parent;
    private int[] rank;
    private int count;
    private Color[] colors;

    public UnionAlgo(int size) {
        parent = new int[size];
        rank = new int[size];
        count = size;
        colors = new Color[size];
        // initialize parent array to be self-referential
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            colors[i] = new Color((int)(Math.random()/100), (int)(Math.random()/100), (int)(Math.random()/100),1);
        }
    }

    public int find(int p) {
        while (p != parent[p]) {
            parent[p] = parent[parent[p]];
            p = parent[p];
        }
        return p;
    }

    public void union(int p, int q) {
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) {
            return;
        }
        if (rank[rootP] < rank[rootQ]) {
            parent[rootP] = rootQ;
            colors[rootP] = colors[rootQ];
        } else if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
            colors[rootQ] = colors[rootP];
        } else {
            parent[rootQ] = rootP;
            rank[rootP]++;
            colors[rootQ] = colors[rootP];
        }
        count--;
    }

    public int count() {
        return count;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    public Color getColor(int p) {
        int rootP = find(p);
        return colors[rootP];
    }

    public void setColor(int p, Color color) {
        int rootP = find(p);
        colors[rootP] = color;
    }
}
