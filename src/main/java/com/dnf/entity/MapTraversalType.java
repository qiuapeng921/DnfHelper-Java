package com.dnf.entity;

import lombok.Data;

/**
 * 地图遍历
 *
 * @author 情歌
 */
@Data
public class MapTraversalType {
    // 地图遍历类型的属性
    public long rwAddr; // 地图地址
    public long mapData; // 地图数据
    public long start; // 起始位置
    public long end; // 结束位置
    public long objNum; // 物体数量
    public long objTmp; // 物体临时变量
    public long objPtr; // 物体指针
    public int objCamp; // 物体阵营
    public long objBlood; // 物体血量
    public int objTypeA; // 物体类型A
    public int objTypeB; // 物体类型B
    public int objCode; // 物体代码
    public String objNameA; // 物体名称A
    public String objNameB; // 物体名称B
    public CoordinateType rwCoordinate; // 地图坐标类型
    public CoordinateType gwCoordinate; // 游戏坐标类型
    public CoordinateType wpCoordinate; // 终点坐标类型
}