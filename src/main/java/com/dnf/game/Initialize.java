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

    @Resource
    private IniUtils iniUtils;


    public void Init() {
        String modelName = "dnf.exe";
        int processId = Process.getProcessId(modelName);
        if (processId == 0) {
            logger.info("等待游戏运行...");
            do {
                processId = Process.getProcessId(modelName);
            } while (processId == 0);
        }

        // 初始化全局计次配置文件
        initConfigIni();

        // 初始化辅助配置文件
        initHelperIni();

        // 设置全局进程id
        memory.setProcessId(processId);
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
    }

    private void initHelperIni() {
        FileUtils fileUtils = new FileUtils(IniConstant.Helper);
        if (!fileUtils.exists()) {
            fileUtils.create();
            iniUtils.write("自动配置", "技能代码", 70231);
            iniUtils.write("自动配置", "技能伤害", 999999);
            iniUtils.write("自动配置", "技能大小", 1);
            iniUtils.write("自动配置", "技能个数", 1);
            iniUtils.write("自动配置", "自动模式", 3);
            iniUtils.write("自动配置", "普通地图", 400001565);
            iniUtils.write("自动配置", "英豪地图", 400001566);
            iniUtils.write("自动配置", "地图难度", 5);
            iniUtils.write("自动配置", "角色数量", 10);
            iniUtils.write("自动配置", "跟随打怪", 3);
            iniUtils.write("自动配置", "过图方式", 2);
            iniUtils.write("自动配置", "处理装备", 1);
            iniUtils.write("自动配置", "开启功能", 0);
            iniUtils.write("自动配置", "出图方式", 3);
            iniUtils.write("自动配置", "过滤物品", "碎布片,金刚石,风化的碎骨,破旧的皮革,血滴石,金刚石,海蓝宝石,黑曜石,最下级砥石,最下级硬化剂,生锈的铁片,鸡腿,肉块,织女星的光辉,赫仑皇帝的印章,幸运兑换币,天才的地图碎片,柴火,玫,远古骑士的盔甲,使徒的气息,坚硬的龟壳,遗留的水晶碎片,[活动]闪亮的雷米援助礼袋 (10个),突变苎麻花叶,副船长的戒指,步枪零件,黑色龙舌兰酒,烤硬的黑面包,虚空魔石碎片,格林赛罗斯的果核,新手HP药剂,新手MP药剂,跃翔药剂,宠物饲料礼盒 (5个),突变草莓,暗黑城特产干酪,艾丽丝的香料,野草莓,卡勒特勋章,下级元素结晶,上级元素结晶,麻辣鲜香麻婆豆腐食盒,迷幻晶石,混沌魔石碎片,碳结晶体,数据芯片,甜蜜巧克力,沁凉雪球,神秘的胶囊碎片,阳光硬币,迷幻晶石,魔刹石,云霓碎片,克尔顿的印章,撒勒的印章,达人HP药剂,达人MP药剂,专家MP药剂,专家HP药剂,熟练MP药剂,熟练HP药剂,血滴石,黑曜石,紫玛瑙,金刚石,海蓝宝石,月饼硬币,暗黑倾向药剂,命运硬币,肉干,砂砾,天空树果实,燃烧瓶,军用回旋镖,裂空镖,甜瓜,飞镖,轰雷树果实,越桔,神圣葡萄酒,轰爆弹,爆弹,燃烧瓶,精灵香精,魔力之花,石头,苎麻花叶,怒海霸主银币,解密礼盒,无尽的永恒,风化的碎骨,破旧的皮革,最下级砥石,最下级硬化剂,生锈的铁片,碎布片,回旋镖,天界珍珠,朗姆酒,飞盘,魔力之花,卡勒特指令书,入门HP药剂,入门MP药剂,普通HP药剂,普通MP药剂,飞盘 2,邪恶药剂,圣杯,肉干,袖珍罐碎片");

            String string = iniUtils.read("自动配置", "技能代码", String.class);
            System.out.println("string = " + string);
        }
    }
}