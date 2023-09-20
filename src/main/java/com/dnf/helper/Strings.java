package com.dnf.helper;

import java.nio.charset.StandardCharsets;
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

    /**
     * 将字符串转换为Unicode编码的字节数组
     *
     * @param ansi 字符串
     * @return Unicode编码的字节数组
     */
    public static int[] asciiToUnicode(String ansi) {
        if (ansi == null || ansi.isEmpty()) {
            return new int[0];
        }
        byte[] bytes = ansi.getBytes(StandardCharsets.UTF_16LE);
        int[] result = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] & 0xFF;
        }
        return result;
    }

    /**
     * 将Unicode编码的字节数组转换为字符串
     *
     * @param unicodeInt Unicode编码的字节数组
     * @return 字符串
     */
    public static String unicodeToAscii(int[] unicodeInt) {
        StringBuilder sb = new StringBuilder();
        for (int codePoint : unicodeInt) {
            //无用数据
            if (codePoint == 0) {
                continue;
            }
            sb.appendCodePoint(codePoint);
            //反括号
            if (codePoint == 93) {
                return sb.toString();
            }
        }
        return sb.toString();
    }

    public static int[] splitToIntArray(String input, String regex) {
        String[] strArray = input.split(regex);
        int[] intArray = new int[strArray.length];

        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }

        return intArray;
    }
}