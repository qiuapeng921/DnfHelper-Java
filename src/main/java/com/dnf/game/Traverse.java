package com.dnf.game;

import com.dnf.entity.MapTraversalType;
import com.dnf.helper.Strings;
import com.dnf.helper.Timer;
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

    /**
     * 组包拾取
     */
    public void packPickup() {
        if (mapData.getStat() != 3) {
            return;
        }

        // 地图遍历数据
        MapTraversalType data = new MapTraversalType();
        data.rwAddr = gamecall.personPtr();
        data.mapData = memory.readLong(memory.readLong(data.rwAddr + Address.DtPyAddr) + 16);
        data.start = memory.readLong(data.mapData + Address.DtKs2);
        data.end = memory.readLong(data.mapData + Address.DtJs2);
        data.objNum = (data.end - data.start) / 24;
        for (data.objTmp = 1; data.objTmp < data.objNum; data.objTmp++) {
            data.objPtr = mapData.getTraversalPtr(data.start, data.objTmp, 2);
            data.objTypeA = memory.readInt(data.objPtr + Address.LxPyAddr);
            data.objTypeB = memory.readInt(data.objPtr + Address.LxPyAddr + 4);
            data.objCamp = memory.readInt(data.objPtr + Address.ZyPyAddr);
            if ((data.objTypeA == 289 || data.objTypeB == 289) && data.objCamp == 200) {
                int[] goodsNameByte = memory.readByte(memory.readLong(memory.readLong(data.objPtr + Address.DmWpAddr) + Address.WpMcAddr), 100);
                data.objNameB = Strings.unicodeToAscii(goodsNameByte);

                if (data.objPtr != data.rwAddr) {
                    int resAddress = mapData.decode(data.objPtr + Address.FbSqAddr);
                    sendPack.pickUp(resAddress);
                }
            }
        }
    }

    // HandleEquip 处理装备
    public void handleEquip() {
//        cfg := helpers.GetConfig().Section("自动配置")
//        handleType := cfg.Key("处理装备").MustUint64(0)
//        if handleType == 0 {
//            return
//        }

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
}
