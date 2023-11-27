package com.dnf.driver.ltq;

import com.dnf.driver.ReadWriteMemory;
import com.sun.jna.Memory;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LtqMemory implements ReadWriteMemory {
    private final Kernel32 kernel32;

    Logger logger = LoggerFactory.getLogger(LtqMemory.class.getName());

    private int processId;


    private WinNT.HANDLE deviceHandle;

    public LtqMemory() {
        kernel32 = Kernel32.INSTANCE;
    }


    @Override
    public void setProcessId(int processId) {
        this.processId = processId;
    }

    @Override
    public long allocate(int size) {
        return 0;
    }

    @Override
    public boolean freed(int address) {
        return false;
    }

    private Memory readMemory(long address, int size) {
        Memory buffer = new Memory(size);

        // 准备输入缓冲区
        ReadWrite inputData = new ReadWrite();
        inputData.setProcessId(processId);
        inputData.setData(buffer);
        inputData.setMemoryAddress(address);
        inputData.setSize(size);
        inputData.setKey("");

        try {
            // 调用 DeviceIoControl 函数
            boolean success = kernel32.DeviceIoControl(deviceHandle, IoCode.READ_CODE, inputData.getPointer(), 40, inputData.getData(), size, new IntByReference(0), null);
            if (!success) {
                throw new Exception(String.format("%d", kernel32.GetLastError()));
            }
            return buffer;
        } catch (Exception e) {
            logger.error("readMemory address = {} , error = {}", address, e.getStackTrace());
            return null;
        }
    }

    @Override
    public int[] readByte(long address, int size) {
        Memory memory = readMemory(address, size);
        if (memory == null) {
            return null;
        }
        int[] memoryValues = new int[size];
        for (int i = 0; i < size; i++) {
            memoryValues[i] = Byte.toUnsignedInt(memory.getByte(i));
        }
        return memoryValues;
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

    private boolean writeMemory(long address, Memory memory, int size) {
        // 准备输入缓冲区
        ReadWrite inputData = new ReadWrite();
        inputData.setProcessId(processId);
        inputData.setData(memory);
        inputData.setMemoryAddress(address);
        inputData.setSize(size);
        inputData.setKey("");

        return kernel32.DeviceIoControl(deviceHandle, IoCode.WRITE_CODE, inputData.getPointer(), 40 + size, null, 0, new IntByReference(0), null);
    }

    @Override
    public boolean writeByte(long address, int[] data) {
        Memory buffer = new Memory(data.length);

        for (int i = 0; i < data.length; i++) {
            byte b = (byte) (data[i] & 0xFF); // 取低8位字节
            buffer.setByte(i, b);
        }

        return writeMemory(address, buffer, data.length);
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

    public boolean installDrive() {
        return true;
    }

    public boolean uninstallDrive() {
        return true;
    }

    public long getModuleAddr() {
        return 0;
    }

    public long getModuleFuncAddr() {
        return 0;
    }

    public long getFuncAddr() {
        return 0;
    }

    public boolean processProtectOn() {
        return true;
    }

    public boolean processProtectOff() {
        return true;
    }
}