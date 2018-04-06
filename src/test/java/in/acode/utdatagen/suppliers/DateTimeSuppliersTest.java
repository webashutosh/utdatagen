package in.acode.utdatagen.suppliers;

import static org.junit.Assert.assertTrue;
import java.time.LocalDateTime;
import java.util.function.Supplier;
import org.junit.Test;

public class DateTimeSuppliersTest {

    @Test
    public void shouldSupplyRandomDateTimeWithinBounds() {
        LocalDateTime lowerBound = LocalDateTime.now();
        LocalDateTime upperBound = LocalDateTime.now().plusDays(10);

        Supplier<LocalDateTime> dateSupplier = DateTimeSuppliers.random(lowerBound, upperBound);
        LocalDateTime prevValue = LocalDateTime.now().minusDays(1);

        for (int i=0; i<10; i++) {
            LocalDateTime date = dateSupplier.get();

            assertTrue(date.compareTo(prevValue) != 0); //make sure that we don't get the same date back-to-back
            assertTrue(date.compareTo(lowerBound) >= 0);
            assertTrue(date.compareTo(upperBound) <= 0);

            prevValue = date;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckIfHigherBoundIsLessThanLowerBound() {
        LocalDateTime lowerBound = LocalDateTime.now();
        LocalDateTime upperBound = LocalDateTime.now().minusDays(1);
        DateTimeSuppliers.random(lowerBound, upperBound);
    }

}