package com.dnf.helper;

import java.io.File;

/**
 * @author 情歌
 */
public class FileUtils {
    private final File file;

    public FileUtils(String filename) {
        file = new File(filename);
    }

    public boolean exists() {
        try {
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean create() {
        try {
            return file.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

    public void remove() {
        file.deleteOnExit();
    }
}
