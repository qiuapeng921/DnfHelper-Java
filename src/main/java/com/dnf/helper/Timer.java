package com.dnf.helper;

/**
 * @author 情歌
 */
public class Timer {
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }
}
