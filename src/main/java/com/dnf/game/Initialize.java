package com.dnf.game;

import cn.hutool.core.date.DateUtil;
import com.dnf.driver.impl.ApiMemory;
import com.dnf.helper.Process;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Initialize {
    Logger logger = LoggerFactory.getLogger(Initialize.class.getName());

    @Resource
    private ApiMemory apiMemory;

    @Resource
    private AutoThread autoThread;

    @Resource
    private Screen screen;


    public void Init() {
        String modelName = "dnf.exe";
        int processId = Process.getProcessId(modelName);
        if (processId == 0) {
            logger.info("等待游戏运行...");
            do {
                processId = Process.getProcessId(modelName);
            } while (processId == 0);
        }

        // 设置全局进程id
        apiMemory.setProcessId(processId);

        logger.info("加载成功-欢迎使用");
        logger.info("当前时间：{}", DateUtil.date(System.currentTimeMillis()));
        hotKey();
    }

    private void hotKey() {
        User32 user32 = User32.INSTANCE;

        user32.RegisterHotKey(null, Win32VK.VK_F1.code, 0, Win32VK.VK_F1.code);
        user32.RegisterHotKey(null, Win32VK.VK_END.code, 0, Win32VK.VK_END.code);
        user32.RegisterHotKey(null, Win32VK.VK_OEM_3.code, 0, Win32VK.VK_OEM_3.code);

        // 消息循环
        WinUser.MSG msg = new WinUser.MSG();
        while (user32.GetMessage(msg, null, 0, 0) != 0) {
            if (msg.message == WinUser.WM_HOTKEY) {
                int hotkeyId = msg.wParam.intValue();
                if (hotkeyId == Win32VK.VK_F1.code) {
                    screen.screenSwitch();
                }
                if (hotkeyId == Win32VK.VK_END.code) {
                    autoThread.autoSwitch();
                }
                if (hotkeyId == Win32VK.VK_OEM_3.code) {
                    screen.screenKill();
                }
            } else {
                user32.TranslateMessage(msg);
                user32.DispatchMessage(msg);
            }
        }
    }
}