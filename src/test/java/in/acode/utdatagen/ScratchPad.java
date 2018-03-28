package in.acode.utdatagen;

import java.math.BigDecimal;
import java.util.UUID;

public class ScratchPad {
    public static void main(String[] args) {
        m("a");
    }

    static void m(String... s) {
        int length = s.length;
        System.out.println(length);
    }

    static void m(String s) {

        System.out.println(s);
    }
}
