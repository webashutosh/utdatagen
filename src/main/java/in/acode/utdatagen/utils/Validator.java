package in.acode.utdatagen.utils;

public class Validator {

    public static void validateBounds(int lower, int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("Lower bound must not be greater than the upper bound");
        }
    }
}
