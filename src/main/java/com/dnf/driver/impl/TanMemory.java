package com.dnf.driver.impl;

import com.dnf.driver.ReadWrite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TanMemory implements ReadWrite {
    private final Logger logger = LoggerFactory.getLogger(TanMemory.class.getName());

    private int processId;

    @Override
    public void setProcessId(int processId) {
        this.processId = processId;
    }

    @Override
    public int[] readByte(long address, int size) {
        return new int[0];
    }

    @Override
    public short readShort(long address) {
        return 0;
    }

    @Override
    public int readInt(long address) {
        return 0;
    }

    @Override
    public long readLong(long address) {
        return 0;
    }

    @Override
    public float readFloat(long address) {
        return 0;
    }

    @Override
    public double readDouble(long address) {
        return 0;
    }

    @Override
    public boolean writeByte(long address, int[] data) {
        return false;
    }

    @Override
    public boolean writeShort(long address, short value) {
        return false;
    }

    @Override
    public boolean writeInt(long address, int value) {
        return false;
    }

    @Override
    public boolean writeLong(long address, long value) {
        return false;
    }

    @Override
    public boolean writeFloat(long address, float value) {
        return false;
    }

    @Override
    public boolean writeDouble(long address, double value) {
        return false;
    }

}
