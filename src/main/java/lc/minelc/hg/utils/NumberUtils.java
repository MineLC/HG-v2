package lc.minelc.hg.utils;

public final class NumberUtils {

    public static int parsePositive(final String text) {
        if (text.isEmpty()) {
            return 0;
        }
        final int length = text.length();
        int result = 0;

        for (int i = 0; i < length; i++) {
            final int value = text.charAt(i) - '0';
            if (value < 0 || value > 9) {
                return -1;
            }
            result = (result + value) * 10;
        }
        return result / 10;
    }
}