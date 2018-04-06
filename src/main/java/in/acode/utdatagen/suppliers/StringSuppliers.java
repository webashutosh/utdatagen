package in.acode.utdatagen.suppliers;

import in.acode.utdatagen.utils.Validator;
import java.util.UUID;
import java.util.function.Supplier;

public class StringSuppliers {

    /**
     * Returns a supplier of random strings within the min and max size limits
     */
    public static Supplier<String> random(int minSize, int maxSize) {
        Validator.validateBounds(minSize, maxSize);

        return () -> {
            String s = UUID.randomUUID().toString();
            while(s.length() < minSize) {
                s += UUID.randomUUID().toString();
            }
            return s.substring(0, Math.min(maxSize, s.length()));
        };
    }

    /**
     * Returns a supplier of random strings within the max size limit
     */
    public static Supplier<String> random(int maxSize) {
        return random(1, maxSize);
    }

}
