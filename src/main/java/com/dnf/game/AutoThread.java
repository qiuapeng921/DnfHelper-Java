package com.dnf.game;

import com.dnf.entity.GlobalData;
import com.dnf.entity.MapDataType;
import com.dnf.helper.Button;
import com.dnf.helper.IniUtils;
import com.dnf.helper.Strings;
import com.dnf.helper.Timer;
import com.sun.jna.platform.win32.Win32VK;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AutoThread extends Base {
    static boolean firstEnterMap; // 首次进图

    static int completedNum;      // 完成次数

    @Resource
    private MapData mapData;

    @Resource
    private GameCall gamecall;

    @Resource
    private Traverse traverse;

    @Resource
    private GameMap gameMap;

    @Resource
    private SendPack sendPack;

    @Resource
    private IniUtils iniUtils;

    @Resource
    private Task task;

    public void autoSwitch() {
        if (!GlobalData.autoSwitch) {
            Thread thread = new Thread(this::autoThread);
            thread.start();
            GlobalData.autoSwitch = true;
            logger.info("自动刷图 - [ √ ]");
        } else {
            GlobalData.autoSwitch = false;
            logger.info("自动刷图 - [ x ]");
        }
    }

    private void autoThread() {
        while (GlobalData.autoSwitch) {
            Timer.sleep(200);
            // 对话处理
            if (mapData.isDialogA() || mapData.isDialogB()) {
                Button.DriveButton(Win32VK.VK_ESCAPE.code, 0, false);
                Timer.sleep(100);
                Button.DriveButton(Win32VK.VK_SPACE.code, 0, false);
                continue;
            }

            // 进入城镇
            if (mapData.getStat() == 0) {
                Timer.sleep(200);
                enterTown();
                continue;
            }

            // 城镇处理
            if (mapData.getStat() == 1 && mapData.isTown()) {
                townHandle();
                continue;
            }

            // 进入副本
            if (mapData.getStat() == 2) {
                enterMap(GlobalData.mapId, GlobalData.mapLevel);
                continue;
            }

            // 在地图内
            if (mapData.getStat() == 3) {
                if (!firstEnterMap && !mapData.isTown()) {
                    //  透明call
                    gamecall.hideCall(gamecall.personPtr());
                    //  sss评分
                    Random random = new Random();
                    int nextInt = random.nextInt(9999999 - 5201314 + 1) + 5201314;
                    memory.writeLong(memory.readLong(Address.PFAddr) + Address.CEPfAddr, nextInt);
                    firstEnterMap = true;
                }

                // 跟随怪物
                if (iniUtils.read("自动配置", "跟随打怪", Integer.class) > 0) {
                    logger.debug("开始跟随怪物");
                    traverse.FollowMonster();
                }

                // 过图
                if (mapData.isOpenDoor() && !mapData.isBossRoom()) {
                    // 捡物品
                    traverse.packPickup();
                    // 过图
                    passMap();
                    continue;
                }

                if (mapData.isBossRoom() && mapData.isPass()) {
                    // 捡物品
                    traverse.packPickup();
                    // 刷图计次
                    passBoss();
                    // 退出副本
                    quitMap();
                    firstEnterMap = false;
                }
            }
        }
    }

    /**
     * 进入城镇
     */
    private void enterTown() {
        Integer roleNumber = iniUtils.read("自动配置", "角色数量", Integer.class);
        GlobalData.roleCount++;
        // 取ini角色数量
        if (GlobalData.roleCount > roleNumber) {
            logger.info("指定角色完成所有角色");
            logger.info("自动刷图 - [ x ]");
            GlobalData.autoSwitch = false;
            return;
        }

        Timer.sleep(200);
        sendPack.selectRole(GlobalData.roleCount);
        Timer.sleep(500);
        logger.info("进入角色 {} ", GlobalData.roleCount);
        logger.info("开始第 [ {} ] 个角色,剩余疲劳 [ {} ]", GlobalData.roleCount + 1, mapData.getPl());

        while (GlobalData.autoSwitch) {
            logger.debug("城镇循环");
            Timer.sleep(300);
            // 进入城镇跳出循环
            if (mapData.getStat() == 1) {
                break;
            }
        }
    }

    /**
     * 城镇处理
     */
    private void townHandle() {
        if (mapData.getPl() < 8) {
            backToRole();
            return;
        }

        Timer.sleep(500);
        // 分解装备
        traverse.handleEquip();

        //  1 剧情 2 搬砖
        Integer autoModel = iniUtils.read("自动配置", "自动模式", Integer.class);
        if (autoModel == 1 && mapData.getRoleLevel() < 110) {
            GlobalData.mapId = task.handleTask();
            GlobalData.mapLevel = 0;
        }
        if (autoModel == 2 && mapData.getRoleLevel() == 110) {
            int[] mapIds;
            if (mapData.getFame() < 25837) {
                String numbers = iniUtils.read("自动配置", "普通地图", String.class);
                mapIds = Strings.splitToIntArray(numbers, ",");
            } else {
                String numbers = iniUtils.read("自动配置", "英豪地图", String.class);
                mapIds = Strings.splitToIntArray(numbers, ",");
            }

            Integer mapLevel = iniUtils.read("自动配置", "地图难度", Integer.class);
            Random random = new Random();
            int index = random.nextInt(mapIds.length);
            GlobalData.mapId = mapIds[index];
            GlobalData.mapLevel = mapLevel;
        }

        if (autoModel == 3) {
            logger.info("未央功能未实现");
            return;
        }

        if (GlobalData.mapId == 0) {
            logger.info("地图编号为空,无法切换区域");
            return;
        }

        Timer.sleep(500);
        gamecall.areaCall(GlobalData.mapId);
        Timer.sleep(500);
        selectMap();
    }

    /**
     * 选择地图
     */
    private void selectMap() {
        while (GlobalData.autoSwitch) {
            logger.debug("选图循环");
            Timer.sleep(200);
            sendPack.selectMap();
            if (mapData.getStat() == 2 || mapData.getStat() == 3) {
                break;
            }
        }
    }

    // 返回角色
    private void backToRole() {
        logger.info("疲劳值不足 · 即将切换角色");
        Timer.sleep(200);
        sendPack.returnRole();
        while (GlobalData.autoSwitch) {
            logger.debug("返回角色循环");
            Timer.sleep(200);
            if (mapData.getStat() == 0) {
                break;
            }
        }
    }

    /**
     * 进入地图
     */
    private void enterMap(int mapId, int mapLevel) {
        if (mapLevel == 5) {
            for (int i = 4; i >= 0; i--) {
                if (mapData.getStat() == 3) {
                    break;
                }
                if (mapData.getStat() == 2) {
                    gamecall.goMapCall(mapId, i);
                    Timer.sleep(1000);
                }
                if (mapData.getStat() == 1) {
                    selectMap();
                }
            }
        } else {
            gamecall.goMapCall(mapId, mapLevel);
        }

        while (GlobalData.autoSwitch) {
            logger.debug("进入副本循环");
            Timer.sleep(200);
            if (mapData.getStat() == 3) {
                break;
            }
        }
    }

    /**
     * 过图处理
     */
    private void passMap() {
        if (!mapData.isOpenDoor() || mapData.isBossRoom()) {
            return;
        }

        int overTheMap = iniUtils.read("自动配置", "过图方式", Integer.class);
        if (overTheMap <= 0) {
            return;
        }

        MapDataType mapDataType = gameMap.mapData();
        int direction = gameMap.getDirection(mapDataType.mapRoute.get(0), mapDataType.mapRoute.get(1));
        if (direction < 0) {
            logger.error("方向错误");
            return;
        }

        switch (overTheMap) {
            case 1:
                gamecall.overMapCall(direction);
            case 2:
//                gamecall.driftCall(direction);
        }
    }

    /**
     * 通过boss
     */
    private void passBoss() {
        IniUtils iniUtils = new IniUtils().setFilename("C:\\config.ini");
        Integer count = iniUtils.read("default", "count", Integer.class);
        iniUtils.write("default", "count", count + 1);
        logger.info("{} [ {} ] 剩余疲劳 [ {} ]", mapData.getMapName(), count + 1, mapData.getPl());
    }

    /**
     * 退出地图
     */
    private void quitMap() {
        Timer.sleep(200);
        Random random = new Random();
        int num = random.nextInt(4);
        sendPack.getIncome(0, num);

        while (GlobalData.autoSwitch) {
            logger.debug("退出副本-处理");
            Timer.sleep(200);
            // 捡物品
            traverse.packPickup();

            // 退出地图
            sendPack.leaveMap();

            // 在城镇或者选图跳出循环
            if (mapData.getStat() == 1 || mapData.getStat() == 2 || mapData.isTown()) {
                break;
            }
        }
    }
}
