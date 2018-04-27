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

        for (int i=0; i<10; i++) {
            LocalDate date = dateSupplier.get();

            assertTrue(date.compareTo(lowerBound) >= 0);
            assertTrue(date.compareTo(upperBound) <= 0);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckIfHigherBoundIsLessThanLowerBound() {
        LocalDate lowerBound = LocalDate.now();
        LocalDate upperBound = LocalDate.now().minusDays(1);
        DateSuppliers.random(lowerBound, upperBound);
    }

}