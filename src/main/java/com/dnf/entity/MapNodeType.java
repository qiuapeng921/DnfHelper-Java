package com.dnf.entity;

import lombok.Data;

// 地图节点
@Data
public class MapNodeType {
    public int f; // 地图F点
    public int g; // 地图G点
    public int h; // 地图H点
    public CoordinateType currentCoordinates; // 当前坐标
    public CoordinateType finalCoordinates; // 最终坐标

    public MapNodeType() {
        this.f = 0;
        this.g = 0;
        this.h = 0;
        this.currentCoordinates = new CoordinateType();
        this.finalCoordinates = new CoordinateType();
    }
}