package com.dnf.helper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * @author 情歌
 */
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < unicodeInt.length - 1; i += 2) {
            if (unicodeInt[i] == 0 && unicodeInt[i + 1] == 0) {
                break;
            }
            int code = unicodeInt[i + 1] << 8 | unicodeInt[i];
            stringBuilder.append((char) code);
        }
        return stringBuilder.toString();
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