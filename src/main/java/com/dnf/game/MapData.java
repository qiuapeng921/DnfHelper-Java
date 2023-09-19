package com.dnf.game;

import com.dnf.driver.impl.ApiMemory;
import com.dnf.entity.CoordinateType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class MapData {

    @Resource
    private ApiMemory apiMemory;

    // 加密
    public void encode(long addr, int data) {
        apiMemory.writeInt(addr, data);
    }

    // 解密
    public long decode(long dataPtr) {
        return apiMemory.readInt(dataPtr);
    }

    public CoordinateType getCutRoom() {
        // 获取当前房间坐标
        CoordinateType result = new CoordinateType();
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = apiMemory.readInt(roomData + Address.CutRoomXAddr);
        result.y = apiMemory.readInt(roomData + Address.CutRoomYAddr);
        return result;
    }

    public CoordinateType getBossRoom() {
        // 获取boss房间坐标
        CoordinateType result = new CoordinateType();
        long roomData = apiMemory.readLong(apiMemory.readLong(apiMemory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        result.x = (int) decode(roomData + Address.BOSSRoomXAddr);
        result.y = (int) decode(roomData + Address.BOSSRoomYAddr);
        return result;
    }
}
