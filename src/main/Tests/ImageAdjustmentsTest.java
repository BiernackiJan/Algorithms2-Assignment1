import org.junit.Assert;
import org.junit.Test;

import static com.example.algorithms2assignment1.ImageAdjustments.*;

public class ImageAdjustmentsTest {
    @Test
    public void swapTest() {
        int[] arr = {1, 2, 3, 4, 5};
        int i = 1;
        int j = 3;
        int[] expectedArr = {1, 4, 3, 2, 5};

        // Call the swap method with the sample input
        swap(arr, i, j);

        // Check if the output is as expected
        Assert.assertArrayEquals(expectedArr, arr);
    }


    @Test
    public void partitioningTest() {
        int[] roots = {3, 1, 4, 2, 5};
        int[] sizes = {10, 20, 30, 40, 50};
        int left = 2;
        int right = 4;
        int expectedPivotIndex = 2;
        int[] expectedRoots = {3, 1, 5, 2, 4};
        int[] expectedSizes = {10, 20, 50, 40, 30};

        // Call the partitioning method with the sample input
        int pivotIndex = partitioning(roots, sizes, left, right);

        // Check if the output is as expected
        Assert.assertEquals(expectedPivotIndex, pivotIndex);
        Assert.assertArrayEquals(expectedRoots, roots);
        Assert.assertArrayEquals(expectedSizes, sizes);
    }

    @Test
    public void fastSortTest() {
        int[] roots = {3, 1, 4, 2, 5};
        int[] sizes = {50, 20, 10, 30, 40};
        int left = 0;
        int right = 4;
        int[] expectedRoots = {3, 5, 2, 1, 4};
        int[] expectedSizes = {50, 40, 30, 20, 10};

        // Call the fastSort method with the sample input
        fastSort(roots, sizes, left, right);

        // Check if the output is as expected
        Assert.assertArrayEquals(expectedRoots, roots);
        Assert.assertArrayEquals(expectedSizes, sizes);
    }

}
