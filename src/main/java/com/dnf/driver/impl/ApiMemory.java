package com.dnf.driver.impl;

import com.dnf.driver.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiMemory implements Memory {
    private final Logger logger = LoggerFactory.getLogger(ApiMemory.class.getName());

    private int processId;

    private final Kernel32 kernel32;

    public ApiMemory() {
        kernel32 = Kernel32.INSTANCE;
    }

    @Override
    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public WinNT.HANDLE openProcess() {
        WinNT.HANDLE handle = kernel32.OpenProcess(0x1F0FFF, false, processId);
        if (handle == null) {
            logger.error("openProcess pid = {} , error = {}", processId, kernel32.GetLastError());
            return null;
        }

        return handle;
    }

    @Override
    public int[] readByteMemory(long address, int size) {
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return null;
        }

        try {
            com.sun.jna.Memory buffer = new com.sun.jna.Memory(size);
            boolean result = kernel32.ReadProcessMemory(handle, new Pointer(address), buffer, size, null);
            if (!result) {
                throw new Exception(String.format("%d", kernel32.GetLastError()));
            }

            int[] memoryValues = new int[size];
            for (int i = 0; i < size; i++) {
                memoryValues[i] = Byte.toUnsignedInt(buffer.getByte(i));
            }

            return memoryValues;
        } catch (Exception e) {
            logger.error("readByteMemory address = {} , error = {}", address, e.getMessage());
        } finally {
            kernel32.CloseHandle(handle);
        }

        return null;
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
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return false;
        }

        int size = data.length;
        com.sun.jna.Memory buffer = new com.sun.jna.Memory(size);
        buffer.write(0, data, 0, size);
        boolean result = kernel32.WriteProcessMemory(handle, new Pointer(address), buffer, size, null);
        if (!result) {
            int error = kernel32.GetLastError();
            logger.error("writeMemory:: address = {} , buffer = {} , error = {}", address, buffer, error);
            return false;
        }

        return true;
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
