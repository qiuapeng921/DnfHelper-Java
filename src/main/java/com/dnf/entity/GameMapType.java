package com.dnf.entity;

// 游戏地图
public class GameMapType {
    public CoordinateType mapCoordinates; // 地图坐标
    public boolean left; // 地图左边
    public boolean right; // 地图右边
    public boolean up; // 地图上边
    public boolean down; // 地图下边
    public int mapChannel; // 地图通道
    public int backgroundColor; // 背景颜色

    public GameMapType() {
        this.mapCoordinates = new CoordinateType();
        this.left = false;
        this.right = false;
        this.up = false;
        this.down = false;
        this.mapChannel = 0;
        this.backgroundColor = 0;
    }
}