package in.acode.utdatagen.suppliers;

import static org.junit.Assert.*;

import java.util.function.Supplier;
import org.junit.Test;

public class IntSuppliersTest {

    @Test
    public void shouldSupplyRandomNumberWithinBounds() {
        int lowerBound = 2, upperBound = 100;
        Supplier<Integer> multipleOf = IntSuppliers.random(lowerBound, upperBound);

        for (int i=0; i<10; i++) {
            Integer num = multipleOf.get();

            assertTrue(num >= lowerBound);
            assertTrue(num <= upperBound);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldCheckIfHigherBoundIsLessThanLowerBound() {
        int lowerBound = 5, upperBound = 4;
        IntSuppliers.random(lowerBound, upperBound);
    }

    @Test
    public void shouldSupplyMultiplesOfANumber() {
        int multiplier = 5, lowerBound = 2, upperBound = 100;
        Supplier<Integer> multipleOf = IntSuppliers.multipleOf(multiplier, lowerBound, upperBound);

        for (int i=0; i<10; i++) {
            Integer num = multipleOf.get();

            assertTrue(num % multiplier == 0);
            assertTrue(num >= lowerBound);
            assertTrue(num <= upperBound);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailToSupplyMultiplesOfANumberWhenNoneExistWithinTheBounds() {
        int multiplier = 10, lowerBound = 2, upperBound = 7;
        Supplier<Integer> multipleOf = IntSuppliers.multipleOf(multiplier, lowerBound, upperBound);
        multipleOf.get();
    }

}