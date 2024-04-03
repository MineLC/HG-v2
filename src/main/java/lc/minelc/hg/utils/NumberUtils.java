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

    public static int aproximate(int value1, int value2) {
        if (value1 < value2) {
            return 1;
        }
        if (value1 % value2 == 0) {
            return value1 / value2;
        }
        return (value1 % value2 == 0)
            ? value1 / value2
            : (value1 / value2) + 1;
    }
}