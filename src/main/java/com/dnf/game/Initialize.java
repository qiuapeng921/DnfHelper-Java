package com.dnf.game;

import cn.hutool.core.date.DateUtil;
import com.dnf.constant.IniConstant;
import com.dnf.helper.FileUtils;
import com.dnf.helper.IniUtils;
import com.dnf.helper.Process;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinUser;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class Initialize extends Base {
    @Resource
    private AutoThread autoThread;

    @Resource
    private Screen screen;

    public void init() {
        String modelName = "dnf.exe";
        int processId = Process.getProcessId(modelName);
        if (processId == 0) {
            logger.info("等待游戏运行...");
            do {
                processId = Process.getProcessId(modelName);
            } while (processId == 0);
        }

        // 初始化配置文件
        initConfigIni();

        // 设置全局进程id
        memory.setProcessId(processId);
        // 判断是否有图标
        if (memory.readInt(0x140000000L) != 9460301) {
            logger.error("无读写权限");
            return;
        }

        initEmptyAddress();

        logger.info("加载成功-欢迎使用");
        logger.info("当前时间：{}", DateUtil.date(System.currentTimeMillis()));
        hotKey();
    }

    /**
     * 注册热键
     */
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
                } else if (hotkeyId == Win32VK.VK_END.code) {
                    autoThread.autoSwitch();
                } else if (hotkeyId == Win32VK.VK_OEM_3.code) {
                    screen.screenKill();
                }
            } else {
                user32.TranslateMessage(msg);
                user32.DispatchMessage(msg);
            }
        }
    }


    /**
     * 初始化空白地址
     */
    private void initEmptyAddress() {
        Address.RwKbAddr = memory.allocate(4096);
        Address.NcBhKbAddr = memory.allocate(4096);
        Address.JnKbAddr = memory.allocate(4096);
        Address.GtKbAddr = memory.allocate(4096);

        logger.info("人物基址 {}", Long.toHexString(Address.RwKbAddr));
        logger.info("内存汇编 {}", Long.toHexString(Address.NcBhKbAddr));
        logger.info("技能空白 {}", Long.toHexString(Address.JnKbAddr));
        logger.info("过图空白 {}", Long.toHexString(Address.GtKbAddr));
    }

    private void initConfigIni() {
        // 判断是否存在全局计次配置文件
        FileUtils fileUtils = new FileUtils(IniConstant.Config);
        if (!fileUtils.exists()) {
            fileUtils.create();
            IniUtils iniUtils = new IniUtils();
            iniUtils.setFilename(IniConstant.Config).write("default", "count", 0);
        }

        FileUtils file = new FileUtils(IniConstant.Helper);
        if (!file.exists()) {
        }
    }
}