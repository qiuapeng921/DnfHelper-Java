package com.dnf.driver;

public interface Memory {
    void setProcessId(int processId);

    int[] readByteMemory(long address, int size);

    short readShort(long address);

    int readInt(long address);

    long readLong(long address);

    float readFloat(long address);

    double readDouble(long address);

    boolean writeByteMemory(long address, int[] data);

    void writeShort(long address, short value);

    void writeInt(long address, int value);

    void writeLong(long address, long value);

    void writeFloat(long address, float value);

    void writeDouble(long address, double value);
}
