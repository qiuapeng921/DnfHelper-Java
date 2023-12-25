package com.dnf.game;

import com.dnf.entity.CoordinateType;
import com.dnf.entity.MapTraversalType;
import com.dnf.helper.*;
import com.sun.jna.platform.win32.Win32VK;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class Traverse extends Base {
    @Resource
    private GameCall gamecall;

    @Resource
    private MapData mapData;

    @Resource
    private SendPack sendPack;

    @Resource
    private IniUtils iniUtils;

    public MapTraversalType getMapData() {
        // 地图遍历数据
        MapTraversalType data = new MapTraversalType();
        data.rwAddr = gamecall.personPtr();
        data.mapData = memory.readLong(memory.readLong(data.rwAddr + Address.DtPyAddr) + 16);
        data.start = memory.readLong(data.mapData + Address.DtKs2);
        data.end = memory.readLong(data.mapData + Address.DtJs2);
        data.objNum = (data.end - data.start) / 24;
        return data;
    }

    /**
     * 组包拾取
     */
    public void packPickup() {
        if (mapData.getStat() != 3) {
            return;
        }

        String itemStr = iniUtils.read("自动配置", "过滤物品", String.class);
        String[] itemArr = itemStr.split(",");

        // 地图遍历数据
        MapTraversalType data = getMapData();
        for (data.objTmp = 1; data.objTmp < data.objNum; data.objTmp++) {
            data.objPtr = mapData.getTraversalPtr(data.start, data.objTmp, 2);
            data.objTypeA = memory.readInt(data.objPtr + Address.LxPyAddr);
            data.objTypeB = memory.readInt(data.objPtr + Address.LxPyAddr + 4);
            data.objCamp = memory.readInt(data.objPtr + Address.ZyPyAddr);
            if ((data.objTypeA == 289 || data.objTypeB == 289) && data.objCamp == 200) {
                int[] goodsNameByte = memory.readByte(memory.readLong(memory.readLong(data.objPtr + Address.DmWpAddr) + Address.WpMcAddr), 100);
                data.objNameB = Strings.unicodeToAscii(goodsNameByte);

                if (Arrays.asList(itemArr).contains(data.objNameB)) {
                    continue;
                }

                if (data.objPtr != data.rwAddr) {
                    int resAddress = mapData.decode(data.objPtr + Address.FbSqAddr);
                    sendPack.pickUp(resAddress);
                }
            }
        }
    }

    // HandleEquip 处理装备
    public void handleEquip() {
        int handleModel = iniUtils.read("自动配置", "处理装备", Integer.class);
        if (handleModel == 0) {
            return;
        }
        if (mapData.backpackWeight() < 60) {
            return;
        }

        int num = 0;
        long address = memory.readLong(memory.readLong(Address.BbJzAddr) + Address.WplPyAddr) + 0x48;

        for (long i = 1; i <= 56; i++) {
            long equip = mapData.getTraversalPtr(address, i, 1);
            if (equip > 0) {
                int equipLevel = memory.readInt(equip + Address.ZbPjAddr);
                int nameAddress = memory.readInt(equip + Address.WpMcAddr);
                String equipName = Strings.unicodeToAscii(memory.readByte(nameAddress, 100));
                if (equipName.isEmpty()) {
                    break;
                }

                // 0白 1蓝
                if (Arrays.asList(0, 1).contains(equipLevel)) {
                    logger.info("分解装备 [ {} ]", equipName);
                    sendPack.decomposition((int) i + 8);
                    Timer.sleep(200);
                    num++;
                }

                // 2紫 3粉
                if (Arrays.asList(2, 3).contains(equipLevel)) {
                    logger.info("出售装备 [ {} ]", equipName);
                    sendPack.sellEquip((int) i + 8);
                    Timer.sleep(200);
                    num++;
                }
            }
        }
        sendPack.tidyBackpack(0, 0);
        logger.info("处理装备 [ {} ] 件", num);
    }

    /**
     * 跟随怪物
     */
    public void followMonster() {

        if (mapData.getStat() != 3) {
            return;
        }

        int followModel = iniUtils.read("自动配置", "跟随打怪", Integer.class);
        int code = iniUtils.read("自动配置", "技能代码", Integer.class);
        int harm = iniUtils.read("自动配置", "技能伤害", Integer.class);
        int size = iniUtils.read("自动配置", "技能大小", Integer.class);

        MapTraversalType data = getMapData();
        for (data.objTmp = 1; data.objTmp < data.objNum; data.objTmp++) {
            data.objPtr = mapData.getTraversalPtr(data.start, data.objTmp, 2);
            if (data.objPtr > 0) {
                data.objTypeA = memory.readInt(data.objPtr + Address.LxPyAddr);
                if (data.objTypeA == 529 || data.objTypeA == 545 || data.objTypeA == 273 || data.objTypeA == 61440 || data.objTypeA == 1057) {
                    data.objCamp = memory.readInt(data.objPtr + Address.ZyPyAddr);
                    data.objCode = memory.readInt(data.objPtr + Address.DmPyAddr);
                    data.objBlood = memory.readLong(data.objPtr + Address.GwXlAddr);
                    if (data.objCamp > 0 && data.objPtr != data.rwAddr) {
                        CoordinateType monster = mapData.readCoordinate(data.objPtr);
                        int[] objNameByte = memory.readByte(memory.readLong(data.objPtr + Address.McPyAddr), 200);
                        data.objNameA = Strings.unicodeToAscii(objNameByte);
                        logger.debug("对象名称:[{}],类型:[{}],阵营:[{}],代码:[{}],血量:[{}],X:[{}],Y:[{}]", data.objNameA, data.objTypeA, data.objCamp, data.objCode, data.objBlood, monster.x, monster.y);

                        if (data.objBlood > 0) {
                            gamecall.driftCall(data.rwAddr, monster.x, monster.y, 0, 0);
                            // 跟随打怪
                            if (followModel == 2) {
                                int[] vkCode = new int[]{Win32VK.VK_A.code, Win32VK.VK_S.code, Win32VK.VK_D.code, Win32VK.VK_F.code, Win32VK.VK_G.code, Win32VK.VK_H.code, Win32VK.VK_Q.code, Win32VK.VK_W.code, Win32VK.VK_E.code, Win32VK.VK_R.code, Win32VK.VK_T.code, Win32VK.VK_Y.code, Win32VK.VK_X.code,};
                                Button.DriveButton(Win32VK.VK_X.code, 1, false);
                                Timer.sleep(800);
                                Button.DriveButton(Win32VK.VK_X.code, 2, false);
                                Timer.sleep(100);
                                int vkCodeRandomIndex = NumberUtils.getRandomNumber(0, vkCode.length - 1);
                                Button.DriveButton(vkCode[vkCodeRandomIndex], 0, false);
                            }
                            // 技能call
                            if (followModel == 3) {
                                Button.DriveButton(Win32VK.VK_X.code, 1, false);
                                Timer.sleep(300);
                                Button.DriveButton(Win32VK.VK_X.code, 2, false);
                                gamecall.skillCall(data.rwAddr, code, harm, monster.x, monster.y, 0, (float) size);
                            }
                        }
                    }
                    Timer.sleep(300);
                }
            }
        }
    }
}
