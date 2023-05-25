package io.github.lunasaw.helper;

import java.util.Arrays;

public class Strings {

    // 取文本左边
    public static String getLeftText(String text, int n) {
        return text.substring(0, n).trim();
    }

    // 取文本右边
    public static String getRightText(String text, int n) {
        String[] parts = text.split("");
        return String.join("", Arrays.copyOfRange(parts, parts.length - n, parts.length));
    }

    // 到整数
    public static int toInteger(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new RuntimeException("到整数失败", e);
        }
    }

    // 到十六进制
    public static String toHex(long num) {
        String hex = Long.toHexString(num);
        return hex.toUpperCase();
    }
}