package com.dnf.helper;

import com.sun.jna.platform.win32.Win32VK;
import org.junit.jupiter.api.Test;

class ButtonTest {

    void driveButton() {
        Timer.sleep(3000);
        Button.DriveButton(Win32VK.VK_LEFT.code, 0, true);
        Button.DriveButton(Win32VK.VK_LEFT.code, 1, true);
        Button.DriveButton(Win32VK.VK_UP.code, 1, true);
        Timer.sleep(3000);
        Button.DriveButton(Win32VK.VK_LEFT.code, 2, true);
        Button.DriveButton(Win32VK.VK_UP.code, 2, true);
    }
}