package com.dnf.game;

import com.dnf.driver.ReadWriteMemory;
import com.dnf.entity.CoordinateType;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Screen {
    Logger logger = LoggerFactory.getLogger(Screen.class.getName());

    @Resource
    private ReadWriteMemory memory;

    @Resource
    private GameCall gameCall;

    @Resource
    private MapData mapData;

    private boolean screenSwitch;

    public void screenSwitch() {
        if (!screenSwitch) {
            Thread thread = new Thread(() -> {
                while (screenSwitch) {
                    Timer.sleep(300);
                    fullScreen();
                }
            });
            thread.start();
            screenSwitch = true;
            logger.info("技能全屏 - [ √ ]");
        } else {
            screenSwitch = false;
            logger.info("技能全屏 - [ x ]");
        }
    }

    private void fullScreen() {
        if (mapData.getStat() != 3) {
            return;
        }

        long personPtr = gameCall.personPtr();
        long map = memory.readLong(memory.readLong(personPtr + Address.DtPyAddr) + 16);
        long start = memory.readLong(map + Address.DtKs2);
        long end = memory.readLong(map + Address.DtJs2);
        long objNum = (end - start) / 24;

        int num = 0;

        for (long i = 1; i <= objNum; i++) {
            long objPtr = mapData.getTraversalPtr(start, i, 2);
            int objType = memory.readInt(objPtr + Address.LxPyAddr);
            int objCamp = memory.readInt(objPtr + Address.ZyPyAddr);

            int obj_code = memory.readInt(objPtr + Address.DmPyAddr);
            if (objType == 529 || objType == 545 || objType == 273 || objType == 61440) {
                long obj_blood = memory.readLong(objPtr + Address.GwXlAddr);
                if (objCamp > 0 && obj_code > 0 && obj_blood > 0 && objPtr != personPtr) {
                    CoordinateType monster = mapData.readCoordinate(objPtr);
                    gameCall.skillCall(personPtr, 70231, 999999, monster.x, monster.y, 0, 1.0F);
                    num++;
                    int number = 10;
                    if (num >= number) break;
                }
            }
        }
    }

    public void screenKill() {
        gameCall.skillCall(0, 54141, 0, 0, 0, 0, 1.0F);
        logger.info("秒杀完毕 [ √ ]");
    }
}
