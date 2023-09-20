package com.dnf.game;

import com.dnf.entity.MapTraversalType;
import com.dnf.helper.Strings;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

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
}
