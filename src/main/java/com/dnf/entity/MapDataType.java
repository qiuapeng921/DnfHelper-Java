package com.dnf.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图数据
 *
 * @author 情歌
 */
@Data
public class MapDataType {
    public String mapName; // 地图名称
    public int mapUm; // 地图编号
    public List<Integer> mapChannel; // 地图通道
    public CoordinateType startZb; // 起始坐标
    public CoordinateType endZb; // 终点坐标
    public int width; // 宽
    public int height; // 高
    public List<CoordinateType> mapRoute; // 地图走法
    public int consumeFatigue; // 消耗疲劳
    public int channelNum; // 通道数量
    public long tmp; // 临时变量

    public MapDataType() {
        this.mapName = "";
        this.mapUm = 0;
        this.mapChannel = new ArrayList<>();
        this.startZb = new CoordinateType();
        this.endZb = new CoordinateType();
        this.width = 0;
        this.height = 0;
        this.mapRoute = new ArrayList<>();
        this.consumeFatigue = 0;
        this.channelNum = 0;
        this.tmp = 0;
    }
}
