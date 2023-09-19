package com.dnf.helper;

public class Timer {
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException ignored) {
        }
    }
}
