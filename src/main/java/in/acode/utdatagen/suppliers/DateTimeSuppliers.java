package in.acode.utdatagen.suppliers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

public class DateTimeSuppliers {

    /**
     * Returns a supplier, which in-turn would supply a random value bounded by the specified upper and lower values
     */
    public static Supplier<LocalDateTime> random(LocalDateTime lowerBoundIncl, LocalDateTime upperBoundIncl) {
        checkBounds(lowerBoundIncl, upperBoundIncl);

        long low = lowerBoundIncl.toEpochSecond(ZoneOffset.UTC);
        long high = upperBoundIncl.toEpochSecond(ZoneOffset.UTC);

        return () -> {
            long rand = low + (long)(Math.random() * (high - low));
            return LocalDateTime.ofEpochSecond(rand, 0, ZoneOffset.UTC);
        };
    }

    private static void checkBounds(LocalDateTime lowerBoundIncl, LocalDateTime upperBoundIncl) {
        if (lowerBoundIncl.compareTo(upperBoundIncl) > 0) {
            throw new IllegalArgumentException("Lower-bound must be >= upper-bound!");
        }
    }

}
