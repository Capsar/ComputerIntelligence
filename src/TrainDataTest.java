import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test suite for the TrainData class
 * Created by Sam van Berkel on 12/09/2018.
 */
public class TrainDataTest {

    @Test
    public void loadDataTest() {
        ArrayList<Product> products = TrainData.loadData("src/files/features.txt", "src/files/targets.txt");
        assertEquals(products.size(), 7854);

        assertEquals(products.get(2).getTarget(), 2);
        assertEquals(products.get(6524).getTarget(), 7);

        assertEquals(products.get(2).getFeatures()[4], 0.11558);
        assertEquals(products.get(6863).getFeatures()[9], 0.23776);
    }
}
