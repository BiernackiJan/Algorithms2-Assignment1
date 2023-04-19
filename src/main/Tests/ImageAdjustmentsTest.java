import com.example.algorithms2assignment1.ImageAdjustments;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ImageAdjustmentsTest {
    @Test
    public void sortSpotsTest() {
        ImageAdjustments adjustments = new ImageAdjustments();
        List<Map.Entry<Integer, Integer>> sortedSpotMap = adjustments.sortSpots(2);
        Assert.assertNotNull(sortedSpotMap);
        Assert.assertFalse(sortedSpotMap.isEmpty());
    }
}
