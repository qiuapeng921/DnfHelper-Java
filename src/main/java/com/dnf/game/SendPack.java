package com.dnf.game;

import com.dnf.helper.Bytes;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class SendPack extends Base {
    @Resource
    private GameCall gameCall;
    private int[] data;

    /**
     * 缓冲call
     *
     * @param param int
     */
    private void hcCall(int param) {
        data = gameCall.subRsp(256);
        data = Bytes.addBytes(data, new int[]{72, 185}, Bytes.intToBytes(Address.FbAddr));
        data = Bytes.addBytes(data, new int[]{186}, Bytes.intToBytes(param));
        data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.HcCallAddr));
        data = Bytes.addBytes(data, new int[]{255, 208});
        data = Bytes.addBytes(data, gameCall.addRsp(256));
    }

    /**
     * 加密call
     *
     * @param param  long
     * @param length int
     */
    private void jmCall(long param, int length) {
        data = Bytes.addBytes(data, gameCall.subRsp(256));
        data = Bytes.addBytes(data, new int[]{72, 185}, Bytes.intToBytes(Address.FbAddr));
        data = Bytes.addBytes(data, new int[]{72, 186}, Bytes.intToBytes(param));
        if (length == 1) {
            data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.JmB1CallAddr));
        }
        if (length == 2) {
            data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.JmB2CallAddr));
        }
        if (length == 4) {
            data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.JmB3CallAddr));
        }
        if (length == 8) {
            data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.JmB4CallAddr));
        }
        data = Bytes.addBytes(data, new int[]{255, 208});
        data = Bytes.addBytes(data, gameCall.addRsp(256));
    }

    /**
     * 发包call
     */
    private void fbCall() {
        data = Bytes.addBytes(data, gameCall.subRsp(256));
        data = Bytes.addBytes(data, new int[]{72, 184}, Bytes.intToBytes(Address.FbCallAddr));
        data = Bytes.addBytes(data, new int[]{255, 208});
        data = Bytes.addBytes(data, gameCall.addRsp(256));
        gameCall.compileCall(data);
        Arrays.fill(data, 0);
    }

    /**
     * 组包返回角色
     */
    public void returnRole() {
        hcCall(7);
        fbCall();
    }

    /**
     * 组包选择角色
     *
     * @param index 角色索引
     */
    public void selectRole(int index) {
        if (index == 0) {
            return;
        }
        hcCall(4);
        jmCall(index, 2);
        fbCall();
    }

    /**
     * 组包选图
     */
    public void selectMap() {
        hcCall(15);
        jmCall(0, 4);
        fbCall();
    }

    /**
     * 组包进图
     *
     * @param bh 地图编号
     * @param nd 难度
     * @param sy 模式
     * @param lx 类型
     */
    public void goMap(long bh, long nd, int sy, int lx) {
        hcCall(16);
        jmCall(bh, 4);
        jmCall(nd, 1);
        jmCall(0, 2);
        jmCall(sy, 1);
        jmCall(lx, 1);
        jmCall(65535, 2);
        jmCall(0, 4);
        jmCall(0, 1);
        jmCall(0, 4);
        jmCall(0, 1);
        jmCall(0, 4);
        fbCall();
    }

    /**
     * 组包翻牌
     *
     * @param h 高位
     * @param l 低位
     */
    public void getIncome(int h, int l) {
        hcCall(69);
        fbCall();
        hcCall(70);
        fbCall();
        hcCall(71);
        jmCall(h, 1);
        jmCall(l, 1);
        fbCall();
        hcCall(1426);
        fbCall();
    }

    /**
     * 组包出图
     */
    public void leaveMap() {
        hcCall(42);
        fbCall();
    }


    /**
     * 组包移动
     *
     * @param maxMap 最大地图编号
     * @param mixMap 最小地图编号
     * @param x      x坐标
     * @param y      y坐标
     */
    public void moveMap(Long maxMap, Long mixMap, Long x, Long y) {
        if (maxMap < 0 || mixMap < 0 || x < 0 || y < 0) {
            return;
        }
        hcCall(36);
        jmCall(maxMap, 4);
        jmCall(mixMap, 4);
        jmCall(x, 2);
        jmCall(y, 2);
        jmCall(5, 1);
        jmCall(38, 4);
        jmCall(0, 2);
        jmCall(0, 4);
        jmCall(0, 1);
        fbCall();
    }

    /**
     * 组包拾取
     *
     * @param addr 地址
     */
    public void pickUp(long addr) {
        if (addr < 0) {
            return;
        }
        hcCall(43);
        jmCall(addr, 4);
        jmCall(0, 1);
        jmCall(1, 1);
        jmCall(566, 2);
        jmCall(291, 2);
        jmCall(9961, 2);
        jmCall(553, 2);
        jmCall(285, 2);
        jmCall(18802, 2);
        jmCall(24743, 2);
        fbCall();
    }

    /**
     * 组包分解
     *
     * @param addr 装备位置
     */
    public void decomposition(int addr) {
        if (addr < 0) {
            return;
        }
        hcCall(26);
        jmCall(0, 1);
        jmCall(65535, 2);
        jmCall(317, 4);
        jmCall(1, 1);
        jmCall(addr, 2);
        fbCall();
    }


    /**
     * 组包出售
     *
     * @param index 出售位置数组
     */
    public void sellEquip(long index) {
        if (index < 0) {
            return;
        }
        hcCall(22);
        jmCall(317, 4);
        jmCall(95, 4);
        jmCall(1, 1);
        jmCall(0, 1);
        jmCall(index, 2);
        jmCall(1, 4);
        jmCall(index * 2 + 2, 4);
        fbCall();
    }

    /**
     * 整理背包
     *
     * @param packType 背包类型
     * @param packAddr 背包地址
     */
    public void tidyBackpack(int packType, int packAddr) {
        hcCall(20);
        jmCall(6, 4);
        jmCall(16, 1);
        // 背包类型:1 装备;2消耗品;3材料;4任务;10副职业
        jmCall(packType, 1);
        // 背包地址:0 背包;2个人仓库;12账号金库
        jmCall(packAddr, 1);
        // 排序方式:0 栏位排序;1品级排序;2Lv排序;3部位排序
        jmCall(packAddr, 1);
        fbCall();
    }

    /**
     * 接受任务
     *
     * @param taskId 任务ID
     */
    public void acceptTask(int taskId) {
        hcCall(31);
        jmCall(31, 2);
        jmCall(taskId, 2);
        fbCall();
    }

    /**
     * 放弃任务
     *
     * @param taskId 任务ID
     */
    public void giveUpTask(int taskId) {
        hcCall(32);
        jmCall(32, 2);
        jmCall(taskId, 2);
        fbCall();
    }

    /**
     * 完成任务
     *
     * @param taskId 任务ID
     */
    public void finishTask(int taskId) {
        hcCall(33);
        jmCall(33, 2);
        jmCall(taskId, 2);
        jmCall(0, 1);
        jmCall(0, 1);
        fbCall();
    }


    /**
     * 提交任务
     *
     * @param taskId 任务ID
     */
    public void submitTask(int taskId) {
        hcCall(34);
        jmCall(34, 2);
        jmCall(taskId, 2);
        jmCall(65535, 2);
        jmCall(1, 2);
        jmCall(65535, 2);
        fbCall();
    }
}
