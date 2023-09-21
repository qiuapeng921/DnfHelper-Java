package com.dnf.game;

import com.dnf.entity.CoordinateType;
import com.dnf.helper.Strings;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class MapData extends Base {
    @Resource
    private GameCall gameCall;

    /**
     * 加密
     *
     * @param address long 内存地址
     * @param value   int 数值
     */
    public void encode(long address, int value) {
        memory.writeInt(address, value);
    }

    /**
     * 解密
     *
     * @param address long 内存地址
     * @return int
     */
    public int decode(long address) {
        return memory.readInt(address);
    }

    /**
     * 游戏状态
     *
     * @return int 0选角 1城镇 2选图 3图内 5选择频道
     */
    public int getStat() {
        return memory.readInt(Address.YXZTAddr);
    }


    /**
     * 是否城镇
     *
     * @return boolean
     */
    public boolean isTown() {
        long personPtr = gameCall.personPtr();
        return memory.readInt(personPtr + Address.DtPyAddr) == 0;
    }


    public boolean isOpenDoor() {
        long personPtr = gameCall.personPtr();
        long encodeData = memory.readLong(memory.readLong(personPtr + Address.DtPyAddr) + 16);
        return decode(encodeData + Address.SfKmAddr) == 0;
    }

    public boolean isBossRoom() {
        CoordinateType cut = getCutRoom();
        CoordinateType boss = getBossRoom();
        return cut.x == boss.x && cut.y == boss.y;
    }

    /**
     * 获取当前房间坐标
     *
     * @return CoordinateType
     */
    public CoordinateType getCutRoom() {
        CoordinateType result = new CoordinateType();
        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = memory.readInt(roomData + Address.CutRoomXAddr);
        result.y = memory.readInt(roomData + Address.CutRoomYAddr);
        return result;
    }

    /**
     * 获取boss房间坐标
     *
     * @return CoordinateType
     */
    public CoordinateType getBossRoom() {
        CoordinateType result = new CoordinateType();
        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = decode(roomData + Address.BOSSRoomXAddr);
        result.y = decode(roomData + Address.BOSSRoomYAddr);
        return result;
    }

    /**
     * 是否通关
     *
     * @return boolean
     */
    public boolean isPass() {
        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        int dataVal = memory.readInt(roomData + Address.GouHuoAddr);
        return dataVal == 2 || dataVal == 0;
    }

    /**
     * 获取疲劳值
     *
     * @return int
     */
    public int getPl() {
        return decode(Address.MaxPlAddr) - decode(Address.CutPlAddr);
    }

    /**
     * 获取角色等级
     *
     * @return int
     */
    public int getRoleLevel() {
        return memory.readInt(Address.JSDjAddr);
    }

    /**
     * 获取地图名称
     *
     * @return string
     */
    public String getMapName() {
        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        int[] mapByte = memory.readByte(memory.readLong(roomData + Address.DtMcAddr), 52);
        return Strings.unicodeToAscii(mapByte);
    }

    /**
     * 读坐标
     *
     * @param param int
     * @return CoordinateType
     */
    public CoordinateType readCoordinate(long param) {
        CoordinateType coordinate = new CoordinateType();
        if (memory.readInt(param + Address.LxPyAddr) == 273) {
            long ptr = memory.readLong(param + Address.DqZbAddr);
            coordinate.setX((int) memory.readFloat(ptr));
            coordinate.setY((int) memory.readFloat(ptr + 4));
            coordinate.setZ((int) memory.readFloat(ptr + 8));
        } else {
            long ptr = memory.readLong(param + Address.FxPyAddr);
            coordinate.setX((int) memory.readFloat(ptr + 32));
            coordinate.setY((int) memory.readFloat(ptr + 36));
            coordinate.setZ((int) memory.readFloat(ptr + 40));
        }
        return coordinate;
    }

    public boolean isDialogA() {
        return memory.readInt(Address.DHAddr) == 1;
    }

    public boolean isDialogB() {
        return memory.readInt(Address.DHAddrB) == 1;
    }

    public boolean isDialogEsc() {
        return memory.readInt(Address.EscDHAddr) == 1;
    }

    /**
     * 背包负重
     *
     * @return int
     */
    public int backpackWeight() {
        long personPtr = gameCall.personPtr();
        long backPackPtr = memory.readLong(personPtr + Address.WplAddr);
        int cutWeight = decode(backPackPtr + Address.DqFzAddr);
        int maxWeight = decode(personPtr + Address.ZdFzAddr);
        float result = (float) cutWeight / maxWeight * 100;
        logger.debug("背包负重: {}", result);
        return (int) result;
    }

    /**
     * 获取名望
     *
     * @return int
     */
    public int getFame() {
        long personPtr = gameCall.personPtr();
        return memory.readInt(personPtr + Address.RwMwAddr);
    }

    /**
     * 取遍历指针
     *
     * @param ptr    long 指针地址
     * @param offset int 漂移计次
     * @param t      int  1 物品 2 地图
     * @return long
     */
    public long getTraversalPtr(long ptr, long offset, int t) {
        long result = 0;

        if (t == 1) {
            long one = memory.readLong(ptr + (offset - 1) * 8L);
            long two = memory.readLong(one - 72);
            result = memory.readLong(two + 16);
        }
        if (t == 2) {
            long one = memory.readLong(ptr + (offset - 1) * 24L);
            result = memory.readLong(one + 16) - 32;
        }

        return result;
    }
}
