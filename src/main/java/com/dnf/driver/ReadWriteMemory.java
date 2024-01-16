package com.dnf.driver;

import java.util.List;

/**
 * @author 情歌
 */
public interface ReadWriteMemory {
    void setProcessId(int processId);

    long allocate(int size);

    boolean freed(int address);

    int[] readByte(long address, int size);

    short readShort(long address);

    int readInt(long address);

    default long readOffset(long address, List<Long> offset) {
        long tmpAddr = address;
        if (offset.isEmpty()) {
            return 0;
        }
        tmpAddr = readLong(tmpAddr);

        for (int i = 1; i < offset.size(); i++) {
            tmpAddr = tmpAddr + offset.get(i);
            tmpAddr = readLong(tmpAddr);
        }

        return tmpAddr;
    }

    long readLong(long address);

    float readFloat(long address);

    double readDouble(long address);

    boolean writeByte(long address, int[] data);

    boolean writeShort(long address, short value);

    boolean writeInt(long address, int value);

    boolean writeLong(long address, long value);

    boolean writeFloat(long address, float value);

    boolean writeDouble(long address, double value);
}
