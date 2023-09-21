package com.dnf.helper;

import java.util.Random;

public class NumberUtils {
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
