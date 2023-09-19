package com.dnf.game;

import com.dnf.driver.impl.ApiMemory;
import com.dnf.entity.CoordinateType;
import com.dnf.helper.Strings;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MapData {
    Logger logger = LoggerFactory.getLogger(MapData.class.getName());

    @Resource
    private ApiMemory apiMemory;

    @Resource
    private GameCall gameCall;

    /**
     * 加密
     * @param address long 内存地址
     * @param value int 数值
     */
    public void encode(long address, int value) {
        apiMemory.writeInt(address, value);
    }

    /**
     * 解密
     * @param address long 内存地址
     * @return int
     */
    public int decode(long address) {
        return apiMemory.readInt(address);
    }

    /**
     * 游戏状态
     *
     * @return int 0选角 1城镇 2选图 3图内 5选择频道
     */
    public int getStat() {
        return apiMemory.readInt(Address.YXZTAddr);
    }


    /**
     * 是否城镇
     *
     * @return boolean
     */
    public boolean isTown() {
        long personPtr = gameCall.personPtr();
        return apiMemory.readInt(personPtr + Address.DtPyAddr) == 0;
    }


    public boolean isOpenDoor() {
        long personPtr = gameCall.personPtr();
        long encodeData = apiMemory.readLong(apiMemory.readLong(personPtr + Address.DtPyAddr) + 16);
        return decode(encodeData + Address.SfKmAddr) == 0;
    }

    public boolean isBossRoom() {
        CoordinateType cut = getCutRoom();
        CoordinateType boss = getBossRoom();
        return cut.x == boss.x && cut.y == boss.y;
    }

    /**
     * 获取当前房间坐标
     * @return CoordinateType
     */
    public CoordinateType getCutRoom() {
        CoordinateType result = new CoordinateType();
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = apiMemory.readInt(roomData + Address.CutRoomXAddr);
        result.y = apiMemory.readInt(roomData + Address.CutRoomYAddr);
        return result;
    }

    /**
     * 获取boss房间坐标
     * @return CoordinateType
     */
    public CoordinateType getBossRoom() {
        CoordinateType result = new CoordinateType();
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = decode(roomData + Address.BOSSRoomXAddr);
        result.y = decode(roomData + Address.BOSSRoomYAddr);
        return result;
    }

    /**
     * 是否通关
     * @return boolean
     */
    public boolean isPass() {
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        int dataVal = apiMemory.readInt(roomData + Address.GouHuoAddr);
        return dataVal == 2 || dataVal == 0;
    }

    /**
     * 获取疲劳值
     * @return int
     */
    public int getPl() {
        return decode(Address.MaxPlAddr) - decode(Address.CutPlAddr);
    }

    /**
     * 获取角色等级
     * @return int
     */
    public int getRoleLevel() {
        return apiMemory.readInt(Address.JSDjAddr);
    }

    /**
     * 获取地图名称
     * @return string
     */
    public String getMapName() {
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        int[] mapByte = apiMemory.readByte(apiMemory.readLong(roomData + Address.DtMcAddr), 52);
        return Strings.unicodeToAscii(mapByte);
    }

    /**
     * 读坐标
     * @param param int
     * @return CoordinateType
     */
    public CoordinateType readCoordinate(int param) {
        CoordinateType coordinate = new CoordinateType();
        if (apiMemory.readInt(param + Address.LxPyAddr) == 273) {
            long ptr = apiMemory.readLong(param + Address.DqZbAddr);
            coordinate.setX((int) apiMemory.readFloat(ptr));
            coordinate.setY((int) apiMemory.readFloat(ptr + 4));
            coordinate.setZ((int) apiMemory.readFloat(ptr + 8));
        } else {
            long ptr = apiMemory.readLong(param + Address.FxPyAddr);
            coordinate.setX((int) apiMemory.readFloat(ptr + 32));
            coordinate.setY((int) apiMemory.readFloat(ptr + 36));
            coordinate.setZ((int) apiMemory.readFloat(ptr + 40));
        }
        return coordinate;
    }

    public boolean isDialogA() {
        return apiMemory.readInt(Address.DHAddr) == 1;
    }

    public boolean isDialogB() {
        return apiMemory.readInt(Address.DHAddrB) == 1;
    }

    public boolean isDialogEsc() {
        return apiMemory.readInt(Address.EscDHAddr) == 1;
    }

    /**
     * 背包负重
     * @return int
     */
    public int backpackWeight() {
        long personPtr = gameCall.personPtr();
        long backPackPtr = apiMemory.readLong(personPtr + Address.WplAddr);
        int cutWeight = decode(backPackPtr + Address.DqFzAddr);
        int maxWeight = decode(personPtr + Address.ZdFzAddr);
        float result = (float) cutWeight / maxWeight * 100;
        return (int) result;
    }

    /**
     * 获取名望
     * @return int
     */
    public int getFame() {
        long personPtr = gameCall.personPtr();
        return apiMemory.readInt(personPtr + Address.RwMwAddr);
    }
}
