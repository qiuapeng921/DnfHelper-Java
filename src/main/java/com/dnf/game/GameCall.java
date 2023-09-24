package com.dnf.game;

import com.dnf.helper.Bytes;
import com.dnf.helper.Timer;
import org.springframework.stereotype.Component;

@Component
public class GameCall extends Base {
    private static boolean compileCallRun;

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
        logger.debug("compiling call {}", intArr);

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
        int[] hookData = memory.readByte(hookShell, 19);
        int[] hookOldData = hookData.clone();

        try {
            hookData = Bytes.addBytes(hookData, new int[]{72, 184}, Bytes.intToBytes(jumpAddress));
            hookData = Bytes.addBytes(hookData, new int[]{131, 56, 1, 117, 42, 72, 129, 236, 0, 3, 0, 0});
            hookData = Bytes.addBytes(hookData, new int[]{72, 187}, Bytes.intToBytes(blankAddress));
            hookData = Bytes.addBytes(hookData, new int[]{255, 211});
            hookData = Bytes.addBytes(hookData, new int[]{72, 184}, Bytes.intToBytes(jumpAddress));
            hookData = Bytes.addBytes(hookData, new int[]{199, 0, 3, 0, 0, 0});
            hookData = Bytes.addBytes(hookData, new int[]{72, 129, 196, 0, 3, 0, 0});
            hookData = Bytes.addBytes(hookData, new int[]{255, 37, 0, 0, 0, 0}, Bytes.intToBytes(hookJump));

            if (memory.readInt(assemblyTransit) == 0) {
                memory.writeByte(assemblyTransit, hookData);
            }

            int[] byteArray = new int[intArr.length];
            System.arraycopy(intArr, 0, byteArray, 0, intArr.length);

            memory.writeByte(blankAddress, Bytes.addBytes(byteArray, new int[]{195}));
            int[] hookShellValue = Bytes.addBytes(new int[]{255, 37, 0, 0, 0, 0}, Bytes.intToBytes(assemblyTransit), new int[]{144, 144, 144, 144, 144});

            memory.writeByte(hookShell, hookShellValue);

            memory.writeInt(jumpAddress, 1);
            while (memory.readInt(jumpAddress) == 1) {
                Timer.sleep(10);
            }
        } catch (Exception e) {
            logger.error("汇编call执行异常 error = {}", (Object) e.getStackTrace());
        } finally {
            memory.writeByte(hookShell, hookOldData);
            memory.writeByte(blankAddress, new int[intArr.length + 16]);
            compileCallRun = false;
        }
    }


    /**
     * 取人物指针call
     *
     * @param address long
     * @return long
     */
    public long getPerPtrCall(long address) {
        // 构造 shellCode 的字节数组
        int[] shellCode = Bytes.addBytes(subRsp(100), call(Address.RWCallAddr), new int[]{72, 163});
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(address));
        shellCode = Bytes.addBytes(shellCode, addRsp(100));
        compileCall(shellCode);
        return memory.readLong(address);
    }

    /**
     * 人物指针
     *
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
    public void skillCall(long address, int code, int harm, int x, int y, int z, float size) {
        // 空白地址
        long emptyAddress = Address.JnKbAddr;
        // 向空白地址写入参数
        memory.writeLong(emptyAddress, address);  // 触发地址
        memory.writeInt(emptyAddress + 16, code);  // 技能代码
        memory.writeInt(emptyAddress + 20, harm);  // 技能伤害
        memory.writeInt(emptyAddress + 32, x);  // x 坐标
        memory.writeInt(emptyAddress + 36, y);  // y 坐标
        memory.writeInt(emptyAddress + 40, z);  // z 坐标
        memory.writeFloat(emptyAddress + 140, size);  // 技能大小
        memory.writeInt(emptyAddress + 144, 65535);  // 最大敌人数量
        memory.writeInt(emptyAddress + 148, 65535);  // 最大敌人数量
        // 构造 shell code
        int[] shellCode = new int[]{72, 129, 236, 0, 2, 0, 0, 72, 185};
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(emptyAddress));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184});
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes(Address.JNCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208, 72, 129, 196, 0, 2, 0, 0});
        compileCall(shellCode);
    }

    /**
     * 透明call
     *
     * @param address long
     */
    public void hideCall(long address) {
        int[] shellCode = new int[]{72, 129, 236, 0, 2, 0, 0};
        shellCode = Bytes.addBytes(shellCode, new int[]{65, 191, 255, 255, 255, 255});
        shellCode = Bytes.addBytes(shellCode, new int[]{199, 68, 36, 32, 255, 255, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{65, 185, 1, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{73, 184, 1, 0, 0, 0, 0, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{186, 1, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 185}, Bytes.intToBytes(address));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.TmCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208, 72, 129, 196, 0, 2, 0, 0});
        compileCall(shellCode);
    }

    /**
     * 区域call
     *
     * @param mapNum long
     */

    public void areaCall(int mapNum) {
        long regionAddress = memory.readLong(Address.QyParamAddr);
        long tmpRegionCall = Address.QyCallAddr;
        int[] shellCode = subRsp(48);
        shellCode = Bytes.addBytes(shellCode, Bytes.addBytes(new int[]{65, 184}, Bytes.intToBytes(mapNum)));
        shellCode = Bytes.addBytes(shellCode, new int[]{186, 174, 12, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184, 255, 255, 255, 255, 0, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, Bytes.addBytes(new int[]{72, 185}, Bytes.intToBytes(Address.QyParamAddr)));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 139, 9});

        shellCode = Bytes.addBytes(shellCode, new int[]{76, 139, 201, 73, 129, 193});
        shellCode = Bytes.addBytes(shellCode, Bytes.intToBytes((int) Address.QyPyAddr), new int[]{73, 131, 233, 64});

        shellCode = Bytes.addBytes(shellCode, Bytes.addBytes(new int[]{72, 184}, Bytes.intToBytes(tmpRegionCall)));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208}, addRsp(48));
        compileCall(shellCode);
        int maxRegion = memory.readInt(regionAddress + Address.QyPyAddr);
        int minRegion = memory.readInt(regionAddress + Address.QyPyAddr + 4);
        int townX = memory.readInt(regionAddress + Address.QyPyAddr + 8);
        int townY = memory.readInt(regionAddress + Address.QyPyAddr + 12);
        moveCall(maxRegion, minRegion, townX, townY);
    }

    /**
     * 移动Call
     *
     * @param maxMap int
     * @param mixMap int
     * @param x      int
     * @param y      int
     */
    public void moveCall(int maxMap, int mixMap, int x, int y) {
        long rolePtr = memory.readLong(Address.JSPtrAddr); // 角色指针
        memory.writeInt(Address.CzSyRdxAddr, maxMap);
        memory.writeInt(Address.CzSyRdxAddr + 4, mixMap);
        memory.writeInt(Address.CzSyRdxAddr + 8, x);
        memory.writeInt(Address.CzSyRdxAddr + 12, y);

        int[] shellCode = subRsp(256);
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 186}, Bytes.intToBytes(Address.CzSyRdxAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 185}, Bytes.intToBytes(rolePtr));
        shellCode = Bytes.addBytes(shellCode, call(Address.CzSyCallAddr)); // 城镇瞬移CALL
        shellCode = Bytes.addBytes(shellCode, addRsp(256));
        compileCall(shellCode);
    }

    /**
     * 过图call
     *
     * @param fx int 0左 1右 2上 3下
     */
    public void overMapCall(int fx) {
        long emptyAddr = Address.GtKbAddr;
        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.StPyAddr);
        int[] shellCode = new int[]{65, 185, 255, 255, 255, 255};
        shellCode = Bytes.addBytes(shellCode, new int[]{73, 184}, Bytes.intToBytes(emptyAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{186}, Bytes.intToBytes(fx));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 185}, Bytes.intToBytes(roomData));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.GtCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208});
        compileCall(shellCode);
    }


    // DriftCall 漂移Call
    public void driftCall(long ptr, int x, int y, int z, int speed) {
        int[] shellCode = new int[]{72, 129, 236, 0, 8, 0, 0};
        shellCode = Bytes.addBytes(shellCode, new int[]{185, 241, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.SqNcCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 139, 240, 72, 139, 200});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.PyCall1Addr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208});
        shellCode = Bytes.addBytes(shellCode, new int[]{185}, Bytes.intToBytes(x));
        shellCode = Bytes.addBytes(shellCode, new int[]{137, 8});
        shellCode = Bytes.addBytes(shellCode, new int[]{185}, Bytes.intToBytes(y));
        shellCode = Bytes.addBytes(shellCode, new int[]{137, 72, 4});
        shellCode = Bytes.addBytes(shellCode, new int[]{185}, Bytes.intToBytes(z));
        shellCode = Bytes.addBytes(shellCode, new int[]{137, 72, 8, 72, 141, 72, 24});
        shellCode = Bytes.addBytes(shellCode, new int[]{186}, Bytes.intToBytes(speed));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.PyCall2Addr));
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 208});
        shellCode = Bytes.addBytes(shellCode, new int[]{51, 219, 137, 95, 48, 199, 135, 224, 0, 0, 0, 2, 0, 0, 0, 72, 141, 69, 136, 72, 137, 68, 36, 96, 72, 137, 93, 136, 72, 137, 93, 144, 51, 210, 72, 141, 77, 136});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 184}, Bytes.intToBytes(Address.XrNcCallAddr));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 139, 206, 72, 139, 1});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 139, 6, 137, 92, 36, 64, 72, 137, 92, 36, 56, 72, 137, 92, 36, 48, 137, 92, 36, 40, 72, 141, 77, 136, 72, 137, 76, 36, 32, 69, 51, 201});
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 186}, Bytes.intToBytes(ptr));
        shellCode = Bytes.addBytes(shellCode, new int[]{73, 184}, Bytes.intToBytes(ptr));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 139, 206});
        shellCode = Bytes.addBytes(shellCode, new int[]{255, 144}, Bytes.intToBytes(312));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 129, 196, 0, 8, 0, 0});
        compileCall(shellCode);
    }


    /**
     * 进图Call
     *
     * @param mapId    int 地图Id
     * @param mapLevel int 地图等级
     */
    public void goMapCall(int mapId, int mapLevel) {
        long rolePtr = memory.readLong(Address.JSPtrAddr);
        int[] shellCode = subRsp(48);
        shellCode = Bytes.addBytes(shellCode, new int[]{65, 185, 0, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, new int[]{65, 184}, Bytes.intToBytes(mapLevel));
        shellCode = Bytes.addBytes(shellCode, new int[]{72, 185}, Bytes.intToBytes(rolePtr));
        shellCode = Bytes.addBytes(shellCode, new int[]{186}, Bytes.intToBytes(mapId));
        shellCode = Bytes.addBytes(shellCode, new int[]{199, 68, 36, 40, 255, 255, 255, 255, 199, 68, 36, 32, 0, 0, 0, 0});
        shellCode = Bytes.addBytes(shellCode, call(Address.JTuCallAddr));
        shellCode = Bytes.addBytes(shellCode, addRsp(48));
        compileCall(shellCode);
    }


    // driftHandle 漂移顺图
    public void driftHandle(int fx) {
        long address = personPtr();
        long mapOffset = memory.readLong(address + Address.DtPyAddr);
        if (mapOffset == 0) {
            return;
        }

        long roomData = memory.readLong(memory.readLong(memory.readLong(Address.FJBHAddr) + Address.SJAddr) + Address.StPyAddr);

        long coordinateStructure = roomData + fx * Address.FxIdAddr + Address.ZbStPyAddr;
        int startX = memory.readInt(coordinateStructure);
        int StartY = memory.readInt(coordinateStructure + 4);
        int endX = memory.readInt(coordinateStructure + 8);
        int endY = memory.readInt(coordinateStructure + 12);
        int x = 0, y = 0;

        switch (fx) {
            case 0:
                x = startX + endX + 20;
                y = StartY + endY / 2;
                break;
            case 1:
                x = startX - 20;
                y = StartY + endY / 2;
                break;
            case 2:
                x = startX + endX / 2;
                y = StartY + endY + 20;
                break;
            case 3:
                x = startX + endX / 2;
                y = StartY - 20;
                break;
        }
        if (x == 0 || y == 0) {
            logger.info("漂移顺图异常");
            return;
        }
        driftCall(address, x, y, 0, 50);
        Timer.sleep(100);
        driftCall(address, startX + endX / 2, StartY, 0, 50);
    }
}
