package com.dnf.driver.impl;

import com.dnf.driver.Memory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TanMemory implements Memory {
    private final Logger logger = LoggerFactory.getLogger(TanMemory.class.getName());

    private int processId;

    @Override
    public void setProcessId(int processId) {
        this.processId = processId;
    }

    @Override
    public int[] readByteMemory(long address, int size) {
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
    public boolean writeByteMemory(long address, int[] data) {
        return false;
    }

    @Override
    public void writeShort(long address, short value) {

    }

    @Override
    public void writeInt(long address, int value) {

    }

    @Override
    public void writeLong(long address, long value) {

    }

    @Override
    public void writeFloat(long address, float value) {

    }

    @Override
    public void writeDouble(long address, double value) {

    }
}
