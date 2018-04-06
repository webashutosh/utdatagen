package in.acode.utdatagen.suppliers;

import static org.junit.Assert.*;

import java.util.function.Supplier;
import org.junit.Test;

public class StringSuppliersTest {

    @Test
    public void shouldSupplyRandomStringWithinMinAndMaxLimits() {
        int minSize = 5, maxSize = 10;
        Supplier<String> supplier = StringSuppliers.random(minSize, maxSize);

        for (int i=0; i<10; i++) {
            String s = supplier.get();

            assertTrue(s.length() >= minSize);
            assertTrue(s.length() <= maxSize);
        }
    }

    @Test
    public void shouldSupplyRandomStringWithinMaxLimit() {
        int maxSize = 50;
        Supplier<String> supplier = StringSuppliers.random(maxSize);

        for (int i=0; i<10; i++) {
            String s = supplier.get();

            assertTrue(s.length() >= 1);
            assertTrue(s.length() <= maxSize);
        }
    }
}