package com.dnf.game;

import com.dnf.driver.ReadWriteMemory;
import com.dnf.entity.CoordinateType;
import com.dnf.entity.GameMapType;
import com.dnf.entity.MapDataType;
import com.dnf.entity.MapNodeType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GameMap extends Base {

    @Resource
    private MapData mapData;

    private static MapNodeType getMapNodeType(CoordinateType mapEnd, CoordinateType waitHandleCoordinate, MapNodeType tmpNode) {
        int guessG;
        if (waitHandleCoordinate.x == tmpNode.currentCoordinates.x || waitHandleCoordinate.y == tmpNode.currentCoordinates.y) {
            guessG = 10;
        } else {
            guessG = 14;
        }
        MapNodeType waitHandleNode = new MapNodeType();
        waitHandleNode.setG(tmpNode.g + guessG);
        waitHandleNode.setH(Math.toIntExact(Math.toIntExact(mapEnd.x) - Math.toIntExact(waitHandleCoordinate.x * 10L) + (mapEnd.y) - Math.toIntExact(waitHandleCoordinate.y * 10L)));
        waitHandleNode.setF(waitHandleNode.g + waitHandleNode.getH());
        waitHandleNode.setCurrentCoordinates(waitHandleCoordinate);
        waitHandleNode.setFinalCoordinates(tmpNode.currentCoordinates);
        return waitHandleNode;
    }

    /**
     * 获取方向
     *
     * @param cutRoom  当前房间
     * @param nextRoom 下一个房间
     * @return 方向
     */
    public int getDirection(CoordinateType cutRoom, CoordinateType nextRoom) {
        int direction = 0;
        int x = cutRoom.x - nextRoom.x;
        int y = cutRoom.y - nextRoom.y;
        if (x == 0 && y == 0) {
            return 4;
        }
        if (x == 0) {
            if (y == 1) {
                direction = 2;
            } else {
                direction = 3;
            }
        } else if (y == 0) {
            if (x == 1) {
                direction = 0;
            } else {
                direction = 1;
            }
        }
        return direction;
    }

    /**
     * 寻路_判断方向
     *
     * @param tx 方向
     * @param fx 方向
     * @return 是否可走
     */
    public boolean judgeDirection(int tx, int fx) {
        // 方向数组
        int[] directionArr = new int[4];
        // 方向集合
        int[][] directionSet = {{0, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 1, 1, 0}, {1, 0, 0, 0}, {1, 1, 0, 0}, {1, 0, 1, 0}, {1, 1, 1, 0}, {0, 0, 0, 1}, {0, 1, 0, 1}, {0, 0, 1, 1}, {0, 1, 1, 1}, {1, 0, 0, 1}, {1, 1, 0, 1}, {1, 0, 1, 1}, {1, 1, 1, 1}};
        if (fx <= 15) {
            System.arraycopy(directionSet[tx], 0, directionArr, 0, 4);
        } else {
            for (int i = 0; i < 4; i++) {
                directionArr[i] = 0;
            }
        }
        return directionArr[fx] == 1;
    }

    /**
     * 整理坐标
     *
     * @param simulationRoute 模拟路线
     * @param realityRoute    真实路线
     * @return 消耗疲劳值
     */
    public int tidyCoordinate(List<CoordinateType> simulationRoute, List<CoordinateType> realityRoute) {
        int x, y, k = 0;
        for (CoordinateType coordinateType : simulationRoute) {
            CoordinateType tempCoordinates = new CoordinateType();
            x = (coordinateType.x + 2) % 3;
            y = (coordinateType.y + 2) % 3;
            if (x == 0 && y == 0) {
                tempCoordinates.x = (coordinateType.x + 2) / 3 - 1;
                tempCoordinates.y = (coordinateType.y + 2) / 3 - 1;
                realityRoute.add(k, tempCoordinates);
                k++;
            }
        }
        return k;
    }

    public MapDataType mapData() {
        ReadWriteMemory mem = memory;
        MapDataType data = new MapDataType();
        //(房间编号)+时间地址)+
        long roomData = mem.readLong(mem.readLong(mem.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.MxPyAddr);
        long roomIndex = mapData.decode(roomData + Address.SyPyAddr);

        data.width = mem.readInt(mem.readLong(roomData + Address.KgPyAddr) + roomIndex * 8);
        data.height = mem.readInt(mem.readLong(roomData + Address.KgPyAddr) + roomIndex * 8 + 4);
        data.tmp = mem.readLong(mem.readLong(roomData + Address.SzPyAddr) + 32 * roomIndex + 8);
        data.channelNum = data.width * data.height;
        for (int i = 0; i < data.channelNum; i++) {
            data.mapChannel.add(i, mem.readInt(data.tmp + i * 4L));
        }

        data.startZb.x = mapData.getCutRoom().x + 1;
        data.startZb.y = mapData.getCutRoom().y + 1;
        data.endZb.x = mapData.getBossRoom().x + 1;
        data.endZb.y = mapData.getBossRoom().y + 1;

        if (data.startZb.x == data.endZb.x && data.startZb.y == data.endZb.y) {
            return data;
        }

        data.consumeFatigue = getRoute(data.mapChannel, data.width, data.height, data.startZb, data.endZb, data.mapRoute);
        return data;
    }

    /**
     * 获取走法
     *
     * @param mapChannel   地图通道
     * @param width        宽
     * @param height       高
     * @param mapStart     起始坐标
     * @param mapEnd       终点坐标
     * @param realityRoute 真实路线
     * @return 消耗疲劳值
     */
    public int getRoute(List<Integer> mapChannel, int width, int height, CoordinateType mapStart, CoordinateType mapEnd, List<CoordinateType> realityRoute) {
        CoordinateType startCoordinate = new CoordinateType();
        CoordinateType endCoordinate = new CoordinateType();

        if (mapStart.x == mapEnd.x && mapStart.y == mapEnd.y) {
            return 0;
        }

        GameMapType[][] mapArray = genMap(width, height, mapChannel);
        GameMapType[][] mapFlag = displayMap(mapArray, width, height);
        startCoordinate.x = mapStart.x * 3 - 2;
        startCoordinate.y = mapStart.y * 3 - 2;
        endCoordinate.x = mapEnd.x * 3 - 2;
        endCoordinate.y = mapEnd.y * 3 - 2;
        List<CoordinateType> crossWay = routeCalculate(mapFlag, startCoordinate, endCoordinate, width * 3, height * 3);
        return tidyCoordinate(crossWay, realityRoute);
    }

    /**
     * 生成地图
     *
     * @param width      宽
     * @param height     高
     * @param mapChannel 地图通道
     * @return 游戏地图
     */
    public GameMapType[][] genMap(int width, int height, List<Integer> mapChannel) {
        GameMapType[][] gameMap = new GameMapType[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                gameMap[x][y] = new GameMapType();
            }
        }

        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                gameMap[x][y].mapCoordinates.x = x;
                gameMap[x][y].mapCoordinates.y = y;
                gameMap[x][y].mapChannel = mapChannel.get(i);
                gameMap[x][y].left = judgeDirection(mapChannel.get(i), 0);
                gameMap[x][y].right = judgeDirection(mapChannel.get(i), 1);
                gameMap[x][y].up = judgeDirection(mapChannel.get(i), 2);
                gameMap[x][y].down = judgeDirection(mapChannel.get(i), 3);
                gameMap[x][y].backgroundColor = 0xFFFFFF;
                i++;
                if (gameMap[x][y].mapChannel == 0) {
                    gameMap[x][y].backgroundColor = 0x000000;
                }
            }
        }

        return gameMap;
    }

    /**
     * 显示地图
     *
     * @param mapArr 地图数组
     * @param width  宽
     * @param height 高
     * @return 游戏地图
     */
    public GameMapType[][] displayMap(GameMapType[][] mapArr, int width, int height) {
        GameMapType[][] mapLabel = new GameMapType[width * 3][height * 3];
        for (int x = 0; x < width * 3; x++) {
            for (int y = 0; y < height * 3; y++) {
                mapLabel[x][y] = new GameMapType();
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                mapLabel[(x + 1) * 3 - 2][(y + 1) * 3 - 2].backgroundColor = 0xFFFFFF;
                if (mapArr[x][y].left) {
                    mapLabel[(x + 1) * 3 - 3][(y + 1) * 3 - 2].backgroundColor = 0xFFFFFF;
                }
                if (mapArr[x][y].right) {
                    mapLabel[(x + 1) * 3 - 1][(y + 1) * 3 - 2].backgroundColor = 0xFFFFFF;
                }
                if (mapArr[x][y].up) {
                    mapLabel[(x + 1) * 3 - 2][(y + 1) * 3 - 3].backgroundColor = 0xFFFFFF;
                }
                if (mapArr[x][y].down) {
                    mapLabel[(x + 1) * 3 - 2][(y + 1) * 3 - 1].backgroundColor = 0xFFFFFF;
                }
            }
        }
        return mapLabel;
    }

    /**
     * 路径计算
     *
     * @param mapLabel 地图标签
     * @param mapStart 起始坐标
     * @param mapEnd   终点坐标
     * @param width    宽
     * @param height   高
     * @return 路径
     */
    public List<CoordinateType> routeCalculate(GameMapType[][] mapLabel, CoordinateType mapStart, CoordinateType mapEnd, int width, int height) {
        MapNodeType tmpNode = new MapNodeType(); // 待检测节点, 临时节点
        List<MapNodeType> openList = new ArrayList<>(); // 开放列表
        List<MapNodeType> closeList = new ArrayList<>(); // 关闭列表

        int shortEstNum = 0; // 最短编号

        tmpNode.currentCoordinates.x = mapStart.x;
        tmpNode.currentCoordinates.y = mapStart.y;

        try {
            mapLabel[Math.toIntExact(mapStart.x)][Math.toIntExact(mapStart.y)].backgroundColor = 0x00FF00;
            mapLabel[Math.toIntExact(mapStart.x)][Math.toIntExact(mapStart.y)].backgroundColor = 0x0000FF;
            openList.add(0, tmpNode);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        List<CoordinateType> moveArr = new ArrayList<>();

        do {
            int minF = 0;
            for (int y = 0; y < openList.size(); y++) {
                if (minF == 0) {
                    minF = openList.get(0).f;
                    shortEstNum = y;
                }
                if (openList.get(y).f < minF) {
                    minF = openList.get(y).f;
                    shortEstNum = y;
                }
            }

            try {
                tmpNode = openList.get(shortEstNum);
                openList.remove(shortEstNum);
                closeList.add(0, tmpNode);
            } catch (RuntimeException e) {
                logger.error(e.getMessage());
            }
            if (tmpNode.currentCoordinates.x != mapStart.x || tmpNode.currentCoordinates.y != mapStart.y) {
                if (tmpNode.currentCoordinates.x != mapEnd.x || tmpNode.currentCoordinates.y != mapEnd.y) {
                    mapLabel[Math.toIntExact(tmpNode.currentCoordinates.x)][Math.toIntExact(tmpNode.currentCoordinates.y)].backgroundColor = 0x0080FF;
                }
            }
            for (int y = 0; y < closeList.size(); y++) {
                if (closeList.get(y).currentCoordinates.x == mapEnd.x && closeList.get(y).currentCoordinates.y == mapEnd.y) {
                    MapNodeType waitHandleNode = closeList.get(y);
                    do {
                        for (MapNodeType mapNodeType : closeList) {
                            if (mapNodeType.currentCoordinates.x == waitHandleNode.finalCoordinates.x && mapNodeType.currentCoordinates.y == waitHandleNode.finalCoordinates.y) {
                                waitHandleNode = mapNodeType;
                                break;
                            }
                        }
                        if (waitHandleNode.currentCoordinates.x != mapStart.x || waitHandleNode.currentCoordinates.y != mapStart.y) {
                            mapLabel[Math.toIntExact(waitHandleNode.currentCoordinates.x)][Math.toIntExact(waitHandleNode.currentCoordinates.y)].backgroundColor = 0x00D8D8;
                            moveArr.add(0, waitHandleNode.currentCoordinates);
                        }

                    } while (waitHandleNode.currentCoordinates.x != mapStart.x || waitHandleNode.currentCoordinates.y != mapStart.y);
                    moveArr.add(0, mapStart);
                    moveArr.add(mapEnd);
                    return moveArr;
                }
            }
            for (int y = 0; y < 4; y++) {
                CoordinateType waitHandleCoordinate = new CoordinateType(); // 待检测坐标
                if (y == 0) {
                    waitHandleCoordinate.x = tmpNode.currentCoordinates.x;
                    waitHandleCoordinate.y = tmpNode.currentCoordinates.y - 1;
                } else if (y == 1) {
                    waitHandleCoordinate.x = tmpNode.currentCoordinates.x - 1;
                    waitHandleCoordinate.y = tmpNode.currentCoordinates.y;
                } else if (y == 2) {
                    waitHandleCoordinate.x = tmpNode.currentCoordinates.x + 1;
                    waitHandleCoordinate.y = tmpNode.currentCoordinates.y;
                } else {
                    waitHandleCoordinate.x = tmpNode.currentCoordinates.x;
                    waitHandleCoordinate.y = tmpNode.currentCoordinates.y + 1;
                }
                if (waitHandleCoordinate.x < 0 || waitHandleCoordinate.x > (width - 1) || waitHandleCoordinate.y < 0 || waitHandleCoordinate.y > (height - 1)) {
                    continue;
                }
                if (mapLabel[Math.toIntExact(waitHandleCoordinate.x)][Math.toIntExact(waitHandleCoordinate.y)].backgroundColor == 0x000000) {
                    continue;
                }
                boolean existCloseList = false;
                for (MapNodeType nodeType : closeList) {
                    if (nodeType.currentCoordinates.x == waitHandleCoordinate.x && nodeType.currentCoordinates.y == waitHandleCoordinate.y) {
                        existCloseList = true;
                        break;
                    }
                }
                if (existCloseList) {
                    continue;
                }
                boolean existOpenList = false;
                for (MapNodeType mapNodeType : openList) {
                    if (mapNodeType.currentCoordinates.x == waitHandleCoordinate.x && mapNodeType.currentCoordinates.y == waitHandleCoordinate.y) {
                        int guessG;
                        if (waitHandleCoordinate.x != tmpNode.currentCoordinates.x || waitHandleCoordinate.y != tmpNode.currentCoordinates.y) {
                            guessG = 14;
                        } else {
                            guessG = 10;
                        }

                        if (tmpNode.g + guessG < mapNodeType.g) {
                            mapNodeType.setFinalCoordinates(tmpNode.currentCoordinates);
                        }

                        existOpenList = true;
                        break;
                    }
                }
                if (!existOpenList) {
                    MapNodeType waitHandleNode = getMapNodeType(mapEnd, waitHandleCoordinate, tmpNode);
                    openList.add(0, waitHandleNode);
                }

            }
        } while (!openList.isEmpty());
        return moveArr;
    }
}
