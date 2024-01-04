package com.dnf.helper;

import java.util.Random;

/**
 * @author 情歌
 */
public class NumberUtils {
    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt((max - min) + 1) + min;
    }
}
