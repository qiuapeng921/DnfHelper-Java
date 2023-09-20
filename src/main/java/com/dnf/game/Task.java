package com.dnf.game;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Task extends Base {

    @Resource
    private MapData mapData;


    /**
     * 处理任务
     *
     * @return long
     */
    public int handleTask() {
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
    public int maxCopy() {
        long level = mapData.getRoleLevel();
        if (level <= 17) {
            if (level <= 3) {
                return 3; //# 雷鸣废墟
            } else if (level <= 4) {
                return 3; //雷鸣废墟
            } else if (level <= 5) {
                return 5; //雷鸣废墟
            } else if (level <= 8) {
                return 6; //猛毒雷鸣废墟
            } else if (level <= 11) {
                return 9; //冰霜幽暗密林
            } else if (level <= 13) {
                return 7; //格拉卡
            } else if (level <= 15) {
                return 8; //烈焰格拉卡
            } else {
                return 1000; //暗黑雷鸣废墟
            }
        }
        // 天空之城
        if (level <= 23) {
            if (level <= 18) {
                return 1000; //# 龙人之塔
            } else if (level <= 19) {
                return 12; //人偶玄关
            } else if (level <= 20) {
                return 13; //石巨人塔
            } else if (level <= 21) {
                return 14; //黑暗玄廊
            } else if (level <= 22) {
                return 17; //悬空城
            } else {
                return 15; //城主宫殿
            }
        }

        // 神殿脊椎
        if (level <= 29) {
            if (level <= 24) {
                return 15; //# 神殿外围
            } else if (level <= 25) {
                return 22; //树精丛林
            } else if (level <= 26) {
                return 23; //炼狱
            } else if (level <= 27) {
                return 24; //极昼
            } else if (level <= 28) {
                return 25; //第一脊椎
            } else {
                return 26; //第二脊椎
            }
        }

        // 神殿脊椎
        if (level <= 35) {
            if (level <= 30) {
                return 26; //# 浅栖之地
            } else if (level <= 31) {
                return 32; //蜘蛛洞穴
            } else if (level <= 32) {
                return 150; //蜘蛛王国
            } else if (level <= 33) {
                return 151; //英雄冢
            } else if (level <= 34) {
                return 35; //暗精灵墓地
            } else {
                return 36; //熔岩穴
            }

        }

        // 暗精灵地区

        // 祭坛
        if (level <= 39) {
            if (level <= 36) {
                return 34; //# 暴君的祭坛
            } else if (level <= 37) {
                return 153; //黄金矿洞
            } else if (level <= 38) {
                return 154; //远古墓穴深处
            } else {
                return 154; //远古墓穴深处
            }

        }


        // 绿都
        if (level <= 49) {
            if (level <= 46) {
                return 141; //# 绿都格罗兹尼
            } else if (level <= 47) {
                return 50; //堕落的盗贼
            } else if (level <= 48) {
                return 51; //迷乱之村哈穆林
            } else {
                return 53; //疑惑之村
            }

        }

        // 绿都
        if (level <= 53) {
            if (level <= 50) {
                return 53; //# 炽晶森林
            } else if (level <= 51) {
                return 145; //冰晶森林
            } else if (level <= 52) {
                return 146; //水晶矿脉
            } else {
                return 148; //幽冥监狱
            }
        }

        // 绿都
        if (level <= 58) {
            if (level <= 54) {
                return 148; //# 蘑菇庄园
            } else if (level <= 55) {
                return 157; //蚁后的巢穴
            } else if (level <= 56) {
                return 158; //腐烂之地
            } else if (level <= 57) {
                return 159; //赫顿玛尔旧街区
            } else {
                return 160; //鲨鱼栖息地
            }
        }

        if (level <= 62) {
            if (level <= 59) {
                return 160; //# 人鱼国度
            } else if (level <= 60) {
                return 163; //GBL女神殿
            } else if (level <= 61) {
                return 164; //树精繁殖地
            } else {
                return 164; //树精繁殖地
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
                return 93; //# 格兰之火
            } else if (level <= 76) {
                return 71; //瘟疫之源
            } else if (level <= 77) {
                return 72; //卡勒特之刃
            } else if (level <= 78) {
                return 74; //绝密区域
            } else if (level <= 79) {
                return 75; //昔日悲鸣
            } else {
                return 76; //凛冬
            }
        }

        if (level <= 85) {
            if (level <= 81) {
                return 76; //102 普鲁兹发电站
            } else if (level <= 82) {
                return 103; //特伦斯发电站
            } else {
                return 104; //格蓝迪发电站
            }
        }

        if (level <= 90) {
            if (level <= 87) {
                return 310; //# 时间广场
            } else if (level <= 88) {
                return 312; //恐怖的栖息地
            } else if (level <= 89) {
                return 314; //恐怖的栖息地
            } else {
                return 314; //红色魔女之森
            }
        }

        if (level <= 100) {
            if (level <= 95) {
                return 291100293; //# 全蚀市场
            } else if (level <= 98) {
                return 291100293; //搏击俱乐部
            }
            return 0;
        }

        if (level <= 109) {
            if (level <= 102) {
                return 100002976; //圣域中心
            } else if (level <= 103) {
                return 100002977; //泽尔峡谷
            } else if (level <= 104) {
                return 100002978; //洛仑山
            } else if (level <= 105) {
                return 100002979; //白色雪原
            } else if (level <= 106) {
                return 100002980; //贝奇的空间
            } else if (level <= 107) {
                return 100002981; //红色魔女之森
            } else if (level <= 108) {
                return 100002982; //红色魔女之森
            } else {
                return 100002983; //红色魔女之森
            }
        }
        return 0;
    }
}
