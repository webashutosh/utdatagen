package in.acode.utdatagen.suppliers;

import java.util.Random;
import java.util.function.Supplier;

public class IntSuppliers {

    /**
     * Returns a supplier, which in-turn would supply a random value bounded by the specified upper and lower values
     */
    public static Supplier<Integer> random(int lowerBoundIncl, int upperBoundIncl) {
        checkBounds(lowerBoundIncl, upperBoundIncl);
        Random random = new Random();

        return () -> random.nextInt(upperBoundIncl - lowerBoundIncl + 1) + lowerBoundIncl;
    }

    /**
     * Returns a supplier, which in-turn would supply a random value, which is a multiple of a specific number
     * The value would be bounded by the specified upper and lower values
     */
    public static Supplier<Integer> multipleOf(int multipleOf, int lowerBoundIncl, int upperBoundIncl) {
        checkBounds(lowerBoundIncl, upperBoundIncl);
        Random random = new Random();

        return () -> {
            int num = random.nextInt(upperBoundIncl - lowerBoundIncl + 1) + lowerBoundIncl;

            num = (num / multipleOf) * multipleOf; //Make the number a multiple of required number

            //The number may slip below the lower-bound, if so, bring-it-up
            if (num < lowerBoundIncl) {
                num = ((lowerBoundIncl / multipleOf) + 1) * multipleOf;

                if (num > upperBoundIncl) {
                    throw new IllegalArgumentException("No multiple of [" + multipleOf + "] "
                        + "fits between [" + lowerBoundIncl + "] and [" + upperBoundIncl + "]");
                }
            }

            return num;
        };
    }

    private static void checkBounds(int lowerBoundIncl, int upperBoundIncl) {
        if (upperBoundIncl < lowerBoundIncl) {
            throw new IllegalArgumentException("Lower-bound must be >= upper-bound!");
        }
    }

}
