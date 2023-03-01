package com.example.algorithms2assignment1;

public class UnionAlgo {
    private int[] parent;
    private int[] rank;

    public UnionAlgo(int size) {
        // Initialize the parent and rank arrays.
        parent = new int[size];
        rank = new int[size];

        // Set each element's parent to itself and rank to 0.
        for (int i = 0; i < size; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public int find(int x) {
        // If x is not the root node, path compress to the root.
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        // Return the root node of the set containing x.
        return parent[x];
    }

    public void union(int x, int y) {
        // Find the root nodes of the sets containing x and y.
        int rootX = find(x);
        int rootY = find(y);

        // If the sets are not already merged, merge them.
        if (rootX != rootY) {
            // Union by rank to keep the tree balanced.
            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }
        }
    }
}
