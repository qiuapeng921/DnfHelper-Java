package com.dnf.game;

import com.dnf.driver.impl.ApiMemory;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class AutoThread {
    /**
     * 首次进图
     */
    private static boolean firstEnterMap;
    Logger logger = LoggerFactory.getLogger(AutoThread.class.getName());
    @Resource
    private ApiMemory apiMemory;
    @Resource
    private MapData mapData;
    /**
     * 自动开关
     */
    private boolean autoSwitch;

    /**
     * 自动开关
     */

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
                    //  透明call todo

                    //  sss评分
                    Random random = new Random();
                    int nextInt = random.nextInt(5201314, 9999999);
                    apiMemory.writeLong(apiMemory.readLong(Address.PFAddr) + Address.CEPfAddr, nextInt);
                    firstEnterMap = true;
                }

                // 跟随怪物 todo

                // 过图
                if (mapData.isOpenDoor() && !mapData.isBossRoom()) {
                    // 捡物品 todo

                    // 过图
                    passMap();
                    continue;
                }

                if (mapData.isBossRoom() && mapData.isPass()) {
                    // 捡物品 todo

                    // 关闭功能 todo

                    // 关闭穿透 todo

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

    }

    /**
     * 进入地图
     */
    private void enterMap(int mapId, int mapLevel) {

    }

    /**
     * 过图处理
     */
    private void passMap() {

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
