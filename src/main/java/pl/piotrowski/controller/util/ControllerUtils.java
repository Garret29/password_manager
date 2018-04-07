package pl.piotrowski.controller.util;

import java.util.Arrays;

public class ControllerUtils {
    public static String mask(String string, char ch) {
        char[] chars = new char[string.length()];
        Arrays.fill(chars, ch);
        return new String(chars);
    }
}
