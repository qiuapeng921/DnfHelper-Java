package com.dnf.game;

import com.dnf.entity.CoordinateType;
import com.dnf.entity.GlobalData;
import com.dnf.entity.MapTraversalType;
import com.dnf.helper.IniUtils;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author 情歌
 */
@Component
public class Screen extends Base {
    @Resource
    private GameCall gameCall;

    @Resource
    private MapData mapData;

    @Resource
    private IniUtils iniUtils;


    public void screenSwitch() {
        if (!GlobalData.screenSwitch) {
            Thread thread = new Thread(() -> {
                while (GlobalData.screenSwitch) {
                    Timer.sleep(300);
                    fullScreen();
                }
            });
            thread.start();
            GlobalData.screenSwitch = true;
            logger.info("技能全屏 - [ √ ]");
        } else {
            GlobalData.screenSwitch = false;
            logger.info("技能全屏 - [ x ]");
        }
    }

    private void fullScreen() {
        if (mapData.getStat() != 3) {
            return;
        }

        // 地图遍历数据
        MapTraversalType data = mapData.getMapData();

        Integer screenCode = iniUtils.read("自动配置", "技能代码", Integer.class);
        Integer screenHarm = iniUtils.read("自动配置", "技能伤害", Integer.class);
        Integer screenSize = iniUtils.read("自动配置", "技能大小", Integer.class);
        Integer screenNumber = iniUtils.read("自动配置", "技能个数", Integer.class);

        int num = 0;

        for (data.objTmp = 1; data.objTmp < data.objNum; data.objTmp++) {
            data.objPtr = mapData.getTraversalPtr(data.start, data.objTmp, 2);
            data.objTypeA = memory.readInt(data.objPtr + Address.LxPyAddr);
            data.objCamp = memory.readInt(data.objPtr + Address.ZyPyAddr);
            data.objCode = memory.readInt(data.objPtr + Address.DmPyAddr);
            if (data.objTypeA == 529 || data.objTypeA == 545 || data.objTypeA == 273 || data.objTypeA == 61440) {
                long objBlood = memory.readLong(data.objPtr + Address.GwXlAddr);
                if (data.objCamp > 0 && data.objCode > 0 && objBlood > 0 && data.objPtr != data.rwAddr) {
                    CoordinateType monster = mapData.readCoordinate(data.objPtr);
                    gameCall.skillCall(data.rwAddr, screenCode, screenHarm, monster.x, monster.y, 0, (float) screenSize);
                    num++;
                    if (num >= screenNumber) {
                        break;
                    }
                }
            }
        }
    }

    public void screenKill() {
        gameCall.skillCall(0, 54141, 0, 0, 0, 0, 1.0F);
        logger.info("秒杀完毕 - [ √ ]");
    }
}
