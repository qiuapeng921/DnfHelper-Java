package com.dnf.game;

import com.dnf.driver.ReadWriteMemory;
import com.dnf.entity.MapDataType;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AutoThread {
    static boolean firstEnterMap; // 首次进图

    Logger logger = LoggerFactory.getLogger(AutoThread.class.getName());

    @Resource
    private ReadWriteMemory memory;

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

    private boolean autoSwitch;

    public void autoSwitch() {
        if (!autoSwitch) {
            Thread thread = new Thread(this::autoThread);
            thread.start();
            autoSwitch = true;
            logger.info("自动刷图 - [ √ ]");
        } else {
            autoSwitch = false;
            logger.info("自动刷图 - [ x ]");
        }
    }

    private void autoThread() {
        while (autoSwitch) {

            Timer.sleep(200);

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
                enterMap(104, 4);
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

                // 跟随怪物 todo

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

    }

    /**
     * 城镇处理
     */
    private void townHandle() {

    }

    /**
     * 选择地图
     */
    private void selectMap() {
        while (autoSwitch) {
            logger.debug("选图循环");
            Timer.sleep(200);
            sendPack.selectMap();
            if (mapData.getStat() == 2 || mapData.getStat() == 3) {
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

        while (autoSwitch) {
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

        MapDataType mapDataType = gameMap.mapData();
        int direction = gameMap.getDirection(mapDataType.mapRoute.get(0), mapDataType.mapRoute.get(1));
        if (direction < 0) {
            logger.error("方向错误");
            return;
        }

        gamecall.overMapCall(direction);
    }

    /**
     * 通过boss
     */
    private void passBoss() {

    }

    /**
     * 退出地图
     */
    private void quitMap() {

    }
}
