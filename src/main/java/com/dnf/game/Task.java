package com.dnf.game;

import com.dnf.entity.GlobalData;
import com.dnf.helper.Strings;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author 情歌
 */
@Component
public class Task extends Base {
    public static String taskName;
    public static String taskCondition;
    public static int taskId;
    @Resource
    private MapData mapData;
    @Resource
    private GameCall gameCall;
    @Resource
    private SendPack sendPack;

    /**
     * 处理任务
     *
     * @return long
     */
    public int handleTask() {
        int mapId;
        int nextTaskId = 0;
        // 无任务刷新角色
        boolean refreshTask = false;
        taskName = "";
        taskCondition = "";
        taskId = 0;

        submitTask();

        while (true) {
            Timer.sleep(200);
            mainLineTask();

            // 处理相同任务输出
            if (taskId != nextTaskId) {
                nextTaskId = taskId;
                logger.info("任务名称 [{}]", taskName);
            }

            // 无任务,刷新角色
            if (taskId == 0) {
                if (!refreshTask) {
                    Timer.sleep(200);
                    logger.info("暂无任务或卡任务,重新选择角色");
                    //返回角色
                    sendPack.returnRole();
                    Timer.sleep(2000);
                    // 选择角色
                    sendPack.selectRole(GlobalData.roleCount - 1);
                    Timer.sleep(500);
                    refreshTask = true;
                    continue;
                } else {
                    mapId = highestMap();
                    logger.info("暂无任务,执行适应等级地图");
                    break;
                }
            }

            refreshTask = false;

            // 任务未接，执行接取任务
            if (finishStatus(taskId) == -1) {
                gameCall.acceptTaskCall(taskId);
            }

            // 跳过部分无法完成任务，取最高等级执行
            // 任务名称[返回赫顿玛尔],任务条件[[seek n meet npc]],任务ID[3509] 材料不足无法完成任务
            // 任务名称[黑市的商人],任务条件[[seek n meet npc]],任务ID[5943] 蛇肉任务
            if (taskId == 3509 || taskId == 5943) {
                mapId = highestMap();
                logger.info("无法完成任务,执行适应等级地图");
                break;
            }

            //  任务完成，执行提交任务
            if (finishStatus(taskId) == 0) {
                gameCall.submitTaskCall(taskId);
                continue;
            }

            // 剧情条件判断
            if (conditional(taskCondition) == 1) {
                gameCall.finishTaskCall(taskId);
            }
            // 刷图任务
            if (conditional(taskCondition) == 2) {
                mapId = taskMap(taskId);
                if (mapId > 0) {
                    break;
                }
            }

            if (conditional(taskCondition) == 3) {
                logger.info("材料任务无法自动完成,执行最高等级地图");
            }
        }

        return mapId;
    }


    /**
     * 主线任务
     */
    public void mainLineTask() {
        long taskAddress = memory.readLong(Address.TaskAddr);
        long start = memory.readLong(taskAddress + Address.QbRwStartAddr);
        long end = memory.readLong(taskAddress + Address.QbRwEndAddr);
        long num = (end - start) / 8;

        for (long i = 0; i < num; i++) {
            long taskPtr = memory.readLong(start + i * 8);
            int taskType = memory.readInt(taskPtr + Address.RwLxAddr);
            if (taskType == 0) {
                int taskLength = memory.readInt(taskPtr + Address.RwDxAddr);
                if (taskLength > 7) {
                    taskName = Strings.unicodeToAscii(memory.readByte(memory.readLong(taskPtr + 16), 100));
                } else {
                    taskName = Strings.unicodeToAscii(memory.readByte(taskPtr + 16, 100));
                }
                // 任务条件
                taskCondition = Strings.unicodeToAscii(memory.readByte(memory.readLong(taskPtr + Address.RwTjAddr), 100));
                // 任务编号
                taskId = memory.readInt(taskPtr);
                break;
            }
        }
    }

    /**
     * 提交任务
     */
    public void submitTask() {
        long taskAddress = memory.readLong(Address.TaskAddr);
        long start = memory.readLong(taskAddress + Address.QbRwStartAddr);
        long end = memory.readLong(taskAddress + Address.QbRwEndAddr);
        long num = (end - start) / 8;

        for (long i = 0; i < num; i++) {
            long taskPtr = memory.readLong(start + i * 8);
            int taskType = memory.readInt(taskPtr + Address.RwLxAddr);
            if (taskType == 0) {
                gameCall.submitTaskCall(memory.readInt(taskPtr));
            }
        }

        start = memory.readLong(taskAddress + Address.YjRwStartAddr);
        end = memory.readLong(taskAddress + Address.YjRwEndAddr);
        num = (end - start) / 16;
        for (long i = 0; i < num; i++) {
            long taskPtr = memory.readLong(start + i * 16);
            int taskType = memory.readInt(taskPtr + Address.RwLxAddr);
            if (taskType == 0) {
                gameCall.submitTaskCall(memory.readInt(taskPtr));
            }
        }
    }

    public long finishStatus(long taskId) {
        long taskAddress = memory.readLong(Address.TaskAddr);
        long start = memory.readLong(taskAddress + Address.YjRwStartAddr);
        long end = memory.readLong(taskAddress + Address.YjRwEndAddr);
        long num = (end - start) / 16;
        long loopEnd = start + num * 16L;

        for (long i = start; i < loopEnd; i += 16L) {
            long taskPtr = memory.readLong(i);
            if (memory.readInt(taskPtr) == taskId) {
                long frequency = mapData.decode(i + 8);
                if (frequency < 512L) {
                    return frequency;
                }
                long[] tmpArr = calculateFrequencyParts(frequency);
                Arrays.sort(tmpArr);
                long sortedMaxPart = tmpArr[tmpArr.length - 1];
                if (sortedMaxPart == 0) {
                    return 1L;
                } else {
                    return sortedMaxPart;
                }
            }
        }

        return -1L;
    }

    private long[] calculateFrequencyParts(long frequency) {
        long[] parts = new long[3];
        parts[0] = frequency % 512L;
        long theRest = frequency - parts[0];

        if (theRest < 262144L) {
            parts[1] = theRest / 512L;
            parts[2] = theRest % 262144L / 512L;
        } else {
            parts[1] = theRest / 262144L;
        }

        return parts;
    }


    /**
     * 任务地图
     */
    public int taskMap(long taskId) {
        long taskAddr = memory.readLong(Address.TaskAddr);
        long start = memory.readLong(taskAddr + Address.YjRwStartAddr);
        long end = memory.readLong(taskAddr + Address.YjRwEndAddr);
        long num = (end - start) / 16;

        for (long i = 0; i < num; i++) {
            long taskPtr = memory.readLong(start + i * 16L);
            if (memory.readInt(taskPtr) == taskId) {
                // 任务副本
                long taskData = memory.readLong(taskPtr + Address.RwFbAddr);
                return memory.readInt(taskData);
            }
        }
        return 0;
    }

    /**
     * conditional_judgment 条件判断
     * 1=城镇完成 比如：对话任务   2=刷图任务，需要进图  3=材料任务
     *
     * @return long
     */
    private int conditional(String conditional) {
        List<String> brushConditions = new ArrayList<>();
        brushConditions.add("[meet npc]");
        brushConditions.add("[seek n meet npc]");
        brushConditions.add("[reach the range]");
        brushConditions.add("[look cinematic]");
        brushConditions.add("[question]");
        brushConditions.add("[quest clear]");
        for (String condition : brushConditions) {
            if (condition.equals(conditional)) {
                return 1;
            }
        }
        brushConditions = new ArrayList<>();
        brushConditions.add("[hunt monster]");
        brushConditions.add("[hunt enemy]");
        brushConditions.add("[condition under clear]");
        brushConditions.add("[clear map]");
        brushConditions.add("[question]");
        brushConditions.add("[seeking]");
        brushConditions.add("[clear dungeon index]");
        for (String condition : brushConditions) {
            if (condition.equals(conditional)) {
                return 2;
            }
        }
        return 0;
    }

    /**
     * 最高等级副本
     *
     * @return long
     */
    public int highestMap() {
        long level = mapData.getRoleLevel();
        if (level <= 17) {
            if (level <= 3) {
                return 3; // # 雷鸣废墟
            } else if (level <= 4) {
                return 3; // 雷鸣废墟
            } else if (level <= 5) {
                return 5; // 雷鸣废墟
            } else if (level <= 8) {
                return 6; // 猛毒雷鸣废墟
            } else if (level <= 11) {
                return 9; // 冰霜幽暗密林
            } else if (level <= 13) {
                return 7; // 格拉卡
            } else if (level <= 15) {
                return 8; // 烈焰格拉卡
            } else {
                return 1000; // 暗黑雷鸣废墟
            }
        }
        //  天空之城
        if (level <= 23) {
            if (level <= 18) {
                return 1000; // # 龙人之塔
            } else if (level <= 19) {
                return 12; // 人偶玄关
            } else if (level <= 20) {
                return 13; // 石巨人塔
            } else if (level <= 21) {
                return 14; // 黑暗玄廊
            } else if (level <= 22) {
                return 17; // 悬空城
            } else {
                return 15; // 城主宫殿
            }
        }

        //  神殿脊椎
        if (level <= 29) {
            if (level <= 24) {
                return 15; // # 神殿外围
            } else if (level <= 25) {
                return 22; // 树精丛林
            } else if (level <= 26) {
                return 23; // 炼狱
            } else if (level <= 27) {
                return 24; // 极昼
            } else if (level <= 28) {
                return 25; // 第一脊椎
            } else {
                return 26; // 第二脊椎
            }
        }

        //  神殿脊椎
        if (level <= 35) {
            if (level <= 30) {
                return 26; // # 浅栖之地
            } else if (level <= 31) {
                return 32; // 蜘蛛洞穴
            } else if (level <= 32) {
                return 150; // 蜘蛛王国
            } else if (level <= 33) {
                return 151; // 英雄冢
            } else if (level <= 34) {
                return 35; // 暗精灵墓地
            } else {
                return 36; // 熔岩穴
            }

        }

        //  暗精灵地区

        //  祭坛
        if (level <= 39) {
            if (level <= 36) {
                return 34; // # 暴君的祭坛
            } else if (level <= 37) {
                return 153; // 黄金矿洞
            } else if (level <= 38) {
                return 154; // 远古墓穴深处
            } else {
                return 154; // 远古墓穴深处
            }

        }


        //  绿都
        if (level <= 49) {
            if (level <= 46) {
                return 141; // # 绿都格罗兹尼
            } else if (level <= 47) {
                return 50; // 堕落的盗贼
            } else if (level <= 48) {
                return 51; // 迷乱之村哈穆林
            } else {
                return 53; // 疑惑之村
            }

        }

        //  绿都
        if (level <= 53) {
            if (level <= 50) {
                return 53; // # 炽晶森林
            } else if (level <= 51) {
                return 145; // 冰晶森林
            } else if (level <= 52) {
                return 146; // 水晶矿脉
            } else {
                return 148; // 幽冥监狱
            }
        }

        //  绿都
        if (level <= 58) {
            if (level <= 54) {
                return 148; // # 蘑菇庄园
            } else if (level <= 55) {
                return 157; // 蚁后的巢穴
            } else if (level <= 56) {
                return 158; // 腐烂之地
            } else if (level <= 57) {
                return 159; // 赫顿玛尔旧街区
            } else {
                return 160; // 鲨鱼栖息地
            }
        }

        if (level <= 62) {
            if (level <= 59) {
                return 160; // # 人鱼国度
            } else if (level <= 60) {
                return 163; // GBL女神殿
            } else if (level <= 61) {
                return 164; // 树精繁殖地
            } else {
                return 164; // 树精繁殖地
            }
        }

        if (level <= 74) {
            if (level <= 71) {
                return 85;
            } else if (level <= 72) {
                return 87;
            } else if (level <= 73) {
                return 92;
            } else {
                return 93;
            }
        }

        if (level <= 80) {
            if (level <= 75) {
                return 93; // # 格兰之火
            } else if (level <= 76) {
                return 71; // 瘟疫之源
            } else if (level <= 77) {
                return 72; // 卡勒特之刃
            } else if (level <= 78) {
                return 74; // 绝密区域
            } else if (level <= 79) {
                return 75; // 昔日悲鸣
            } else {
                return 76; // 凛冬
            }
        }

        if (level <= 85) {
            if (level <= 81) {
                return 76; // 102 普鲁兹发电站
            } else if (level <= 82) {
                return 103; // 特伦斯发电站
            } else {
                return 104; // 格蓝迪发电站
            }
        }

        if (level <= 90) {
            if (level <= 87) {
                return 310; // # 时间广场
            } else if (level <= 88) {
                return 312; // 恐怖的栖息地
            } else if (level <= 89) {
                return 314; // 恐怖的栖息地
            } else {
                return 314; // 红色魔女之森
            }
        }

        if (level <= 100) {
            if (level <= 95) {
                return 291100293; // # 全蚀市场
            } else if (level <= 98) {
                return 291100293; // 搏击俱乐部
            }
            return 0;
        }

        if (level <= 109) {
            if (level <= 102) {
                return 100002976; // 圣域中心
            } else if (level <= 103) {
                return 100002977; // 泽尔峡谷
            } else if (level <= 104) {
                return 100002978; // 洛仑山
            } else if (level <= 105) {
                return 100002979; // 白色雪原
            } else if (level <= 106) {
                return 100002980; // 贝奇的空间
            } else if (level <= 107) {
                return 100002981; // 红色魔女之森
            } else if (level <= 108) {
                return 100002982; // 红色魔女之森
            } else {
                return 100002983; // 红色魔女之森
            }
        }
        return 0;
    }
}