package in.acode.utdatagen.suppliers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.function.Supplier;

public class DateSuppliers {

    /**
     * Returns a supplier, which in-turn would supply a random value bounded by the specified upper and lower values
     */
    public static Supplier<LocalDate> random(LocalDate lowerBoundIncl, LocalDate upperBoundIncl) {
        checkBounds(lowerBoundIncl, upperBoundIncl);

        int daysDiff = (int)ChronoUnit.DAYS.between(lowerBoundIncl, upperBoundIncl);
        Random random = new Random();

        return () -> {
            int rand = random.nextInt(daysDiff + 1);
            return lowerBoundIncl.plusDays(rand);
        };
    }

    private static void checkBounds(LocalDate lowerBoundIncl, LocalDate upperBoundIncl) {
        if (lowerBoundIncl.compareTo(upperBoundIncl) > 0) {
            throw new IllegalArgumentException("Lower-bound must be >= upper-bound!");
        }
    }

}
