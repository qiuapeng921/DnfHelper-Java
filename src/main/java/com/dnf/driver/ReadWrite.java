package com.dnf.driver;

public interface ReadWrite {
    void setProcessId(int processId);

    int[] readByte(long address, int size);

    short readShort(long address);

    int readInt(long address);

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
