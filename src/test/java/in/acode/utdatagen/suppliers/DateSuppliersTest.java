package in.acode.utdatagen.suppliers;

import static org.junit.Assert.assertTrue;
import java.time.LocalDate;
import java.util.function.Supplier;
import org.junit.Test;

public class DateSuppliersTest {

    @Test
    public void shouldSupplyRandomDateWithinBounds() {
        LocalDate lowerBound = LocalDate.now();
        LocalDate upperBound = LocalDate.now().plusDays(10);

        Supplier<LocalDate> dateSupplier = DateSuppliers.random(lowerBound, upperBound);
        LocalDate prevValue = LocalDate.now().minusDays(1);

        for (int i=0; i<10; i++) {
            LocalDate date = dateSupplier.get();

            assertTrue(date.compareTo(prevValue) != 0); //make sure that we don't get the same date back-to-back
            assertTrue(date.compareTo(lowerBound) >= 0);
            assertTrue(date.compareTo(upperBound) <= 0);

            prevValue = date;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckIfHigherBoundIsLessThanLowerBound() {
        LocalDate lowerBound = LocalDate.now();
        LocalDate upperBound = LocalDate.now().minusDays(1);
        DateSuppliers.random(lowerBound, upperBound);
    }

}