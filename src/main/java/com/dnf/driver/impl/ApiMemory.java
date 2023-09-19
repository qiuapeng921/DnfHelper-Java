package com.dnf.driver.impl;

import com.dnf.driver.ReadWrite;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.sun.jna.Memory;

@Component
public class ApiMemory implements ReadWrite {
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
        WinNT.HANDLE handle = kernel32.OpenProcess(Kernel32.PROCESS_ALL_ACCESS, false, processId);
        if (handle == null) {
            logger.error("openProcess pid = {} , error = {}", processId, kernel32.GetLastError());
            return null;
        }

        return handle;
    }

    @Override
    public long allocate(int size) {
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return 0;
        }
        Pointer pointer = kernel32.VirtualAllocEx(handle, new Pointer(0), new BaseTSD.SIZE_T(size), 4096, 64);
        return pointer.getLong(0);
    }

    @Override
    public boolean freed(int address) {
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return false;
        }
        return kernel32.VirtualFreeEx(handle, new Pointer(address), new BaseTSD.SIZE_T(0), WinNT.MEM_RELEASE);
    }

    private Memory readMemory(long address, int size) {
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return null;
        }

        try {
            // 创建缓冲区，并将数据放入其中
            IntByReference bytesWritten = new IntByReference();
            Memory buffer = new Memory(size);
            boolean result = kernel32.ReadProcessMemory(handle, new Pointer(address), buffer, size, bytesWritten);
            if (!result) {
                throw new Exception(String.format("%d", kernel32.GetLastError()));
            }
            return buffer;
        } catch (Exception e) {
            logger.error("readByteMemory address = {} , error = {}", address, e.getMessage());
            return null;
        } finally {
            kernel32.CloseHandle(handle);
        }
    }

    @Override
    public int[] readByte(long address, int size) {
        Memory memory = readMemory(address, size);
        int[] memoryValues = new int[size];
        for (int i = 0; i < size; i++) {
            memoryValues[i] = Byte.toUnsignedInt(memory.getByte(i));
        }
        return memoryValues;
    }

    @Override
    public short readShort(long address) {
        Memory memory = readMemory(address, 2);
        return memory.getShort(0);
    }

    @Override
    public int readInt(long address) {
        Memory memory = readMemory(address, 4);
        return memory.getInt(0);
    }

    @Override
    public long readLong(long address) {
        Memory memory = readMemory(address, 8);
        return memory.getLong(0);
    }

    @Override
    public float readFloat(long address) {
        Memory memory = readMemory(address, 4);
        return memory.getFloat(0);
    }

    @Override
    public double readDouble(long address) {
        Memory memory = readMemory(address, 8);
        return memory.getDouble(0);
    }


    private boolean writeMemory(long address, Memory memory, int size) {
        WinNT.HANDLE handle = openProcess();
        if (handle == null) {
            return false;
        }

        try {
            // 写入数据
            boolean result = kernel32.WriteProcessMemory(handle, new Pointer(address), memory, size, null);
            if (!result) {
                throw new Exception(String.format("%d", kernel32.GetLastError()));
            }
            return true;
        } catch (Exception e) {
            logger.error("writeByteMemory address = {} , error = {}", address, e.getMessage());
            return false;
        } finally {
            kernel32.CloseHandle(handle);
        }
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
        Memory buffer = new Memory(2);
        buffer.setShort(0, value);
        return writeMemory(address, buffer, 2);
    }

    @Override
    public boolean writeInt(long address, int value) {
        Memory buffer = new Memory(4);
        buffer.setInt(0, value);
        return writeMemory(address, buffer, 4);
    }

    @Override
    public boolean writeLong(long address, long value) {
        Memory buffer = new Memory(8);
        buffer.setLong(0, value);
        return writeMemory(address, buffer, 8);
    }

    @Override
    public boolean writeFloat(long address, float value) {
        Memory buffer = new Memory(4);
        buffer.setFloat(0, value);
        return writeMemory(address, buffer, 4);
    }

    @Override
    public boolean writeDouble(long address, double value) {
        Memory buffer = new Memory(8);
        buffer.setDouble(0, value);
        return writeMemory(address, buffer, 8);
    }
}
