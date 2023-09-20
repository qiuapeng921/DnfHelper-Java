package com.dnf.helper;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

public class Button {

    /**
     * 驱动按键
     *
     * @param vkCode   int 按键码
     * @param sendType int 按键方式 0按下+抬起  1按下 2抬起
     * @param funcType boolean 功能键方式 true为长按
     */
    public static void DriveButton(int vkCode, int sendType, boolean funcType) {
        int coverCode = User32.INSTANCE.MapVirtualKeyEx(vkCode, 0, null);

        WinUser.KEYBDINPUT keyboardInput = new WinUser.KEYBDINPUT();
        keyboardInput.wVk = new WinDef.WORD(vkCode);
        keyboardInput.wScan = new WinDef.WORD(coverCode);

        if (sendType == 0 || sendType == 1) {
            keyboardInput.time = new WinDef.DWORD(System.currentTimeMillis() / 1000);
            keyboardInput.dwFlags = new WinDef.DWORD(funcType ? 0x1 : 0x0);
            sendInput(keyboardInput);
            Timer.sleep(10);
        }

        if (sendType == 0 || sendType == 2) {
            keyboardInput.time = new WinDef.DWORD(System.currentTimeMillis() / 1000);
            keyboardInput.dwFlags = new WinDef.DWORD(funcType ? 0x3 : 0x2);
            sendInput(keyboardInput);
            Timer.sleep(10);
        }
    }

    private static void sendInput(WinUser.KEYBDINPUT keyboardInput) {
        WinUser.INPUT inputPress = new WinUser.INPUT();
        inputPress.type = new WinDef.DWORD(1);
        inputPress.input.setType("ki");
        inputPress.input.ki = keyboardInput;
        User32.INSTANCE.SendInput(new WinDef.DWORD(1), new WinUser.INPUT[]{inputPress}, inputPress.size());
    }
}
