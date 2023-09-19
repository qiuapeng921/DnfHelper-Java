package com.dnf.game;

import com.dnf.driver.impl.ApiMemory;
import com.dnf.helper.Bytes;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GameCall {
    Logger logger = LoggerFactory.getLogger(GameCall.class.getName());

    @Resource
    private ApiMemory apiMemory;

    private boolean compileCallRun;

    public int[] subRsp(int i) {
        if (i > 127) {
            return Bytes.addBytes(new int[]{72, 129, 236}, Bytes.intToBytes(i));
        }
        return Bytes.addBytes(new int[]{72, 131, 236}, new int[]{i});
    }

    public int[] addRsp(int i) {
        if (i > 127) {
            return Bytes.addBytes(new int[]{72, 129, 196}, Bytes.intToBytes(i));
        }
        return Bytes.addBytes(new int[]{72, 131, 196}, new int[]{i});
    }

    public int[] call(long address) {
        int[] shellCode = new int[]{255, 21, 2, 0, 0, 0, 235, 8};
        return Bytes.addBytes(shellCode, Bytes.intToBytes(address));
    }

    /**
     * 汇编call
     *
     * @param intArr int[]
     */
    public void compileCall(int[] intArr) {
        // 汇编中转, 空白地址, 跳转地址
        long assemblyTransit = Address.NcBhKbAddr + 300;
        long blankAddress = Address.NcBhKbAddr + 500;
        long jumpAddress = blankAddress - 100;

        if (compileCallRun) {
            return;
        }

        compileCallRun = true;
        long hookShell = Address.HBCallAddr;
        hookShell = hookShell + 144;
        long hookJump = hookShell + 19;
        int[] hookData = apiMemory.readByte(hookShell, 19);
        int[] hookOldData = hookData.clone();

        hookData = Bytes.addBytes(hookData, new int[]{72, 184}, Bytes.intToBytes(jumpAddress));
        hookData = Bytes.addBytes(hookData, new int[]{131, 56, 1, 117, 42, 72, 129, 236, 0, 3, 0, 0});
        hookData = Bytes.addBytes(hookData, new int[]{72, 187}, Bytes.intToBytes(blankAddress));
        hookData = Bytes.addBytes(hookData, new int[]{255, 211});
        hookData = Bytes.addBytes(hookData, new int[]{72, 184}, Bytes.intToBytes(jumpAddress));
        hookData = Bytes.addBytes(hookData, new int[]{199, 0, 3, 0, 0, 0});
        hookData = Bytes.addBytes(hookData, new int[]{72, 129, 196, 0, 3, 0, 0});
        hookData = Bytes.addBytes(hookData, new int[]{255, 37, 0, 0, 0, 0}, Bytes.intToBytes(hookJump));

        if (apiMemory.readInt(assemblyTransit) == 0) {
            apiMemory.writeByte(assemblyTransit, hookData);
        }

        int[] byteArray = new int[intArr.length];
        System.arraycopy(intArr, 0, byteArray, 0, intArr.length);

        apiMemory.writeByte(blankAddress, Bytes.addBytes(byteArray, new int[]{195}));
        int[] hookShellValue = Bytes.addBytes(new int[]{255, 37, 0, 0, 0, 0}, Bytes.intToBytes(assemblyTransit), new int[]{144, 144, 144, 144, 144});

        apiMemory.writeByte(hookShell, hookShellValue);

        apiMemory.writeInt(jumpAddress, 1);
        while (apiMemory.readInt(jumpAddress) == 1) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }

        apiMemory.writeByte(hookShell, hookOldData);
        apiMemory.writeByte(blankAddress, new int[intArr.length + 16]);
        compileCallRun = false;
    }


    /**
     * 取人物指针call
     * @param address long
     * @return long
     */
    public long getPerPtrCall(long address) {
        // 构造 shellCode 的字节数组
        int[] shellCode = Bytes.addBytes(subRsp(100), call(Address.RWCallAddr), new int[]{72, 163});
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(address));
        shellCode = Bytes.addBytes(shellCode, addRsp(100));
        compileCall(shellCode);
        return apiMemory.readLong(address);
    }

    /**
     * 人物指针
     * @return long
     */
    public long personPtr() {
        return getPerPtrCall(Address.RwKbAddr);
    }

    /**
     * 技能call
     *
     * @param address 触发地址
     * @param code    技能代码
     * @param harm    技能伤害
     * @param x       x坐标
     * @param y       y坐标
     * @param z       z坐标
     * @param size    技能大小
     */
    public void skillCall(int address, int code, int harm, int x, int y, int z, float size) {
        // 空白地址
        long emptyAddress = Address.JnKbAddr;
        // 向空白地址写入参数
        apiMemory.writeLong(emptyAddress, address);  // 触发地址
        apiMemory.writeInt(emptyAddress + 16, code);  // 技能代码
        apiMemory.writeInt(emptyAddress + 20, harm);  // 技能伤害
        apiMemory.writeInt(emptyAddress + 32, x);  // x 坐标
        apiMemory.writeInt(emptyAddress + 36, y);  // y 坐标
        apiMemory.writeInt(emptyAddress + 40, z);  // z 坐标
        apiMemory.writeFloat(emptyAddress + 140, size);  // 技能大小
        apiMemory.writeInt(emptyAddress + 144, 65535);  // 最大敌人数量
        apiMemory.writeInt(emptyAddress + 148, 65535);  // 最大敌人数量
        // 构造 shell code
        int[] shellCode = new int[]{72, 129, 236, 0, 2, 0, 0, 72, 185};
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(emptyAddress));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184});
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(Address.JNCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208, 72, 129, 196, 0, 2, 0, 0});
        compileCall(shellCode);
    }
}
