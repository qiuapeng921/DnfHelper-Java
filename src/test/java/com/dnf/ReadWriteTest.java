package com.dnf;

import com.dnf.driver.impl.ApiMemory;
import com.dnf.helper.Process;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ReadWriteTest {

    @Autowired
    private ApiMemory apiMemory;

    @Test
    public void test() {
        String modelName = "dnf.exe";
        int processId = Process.getProcessId(modelName);
        // 设置全局进程id
        apiMemory.setProcessId(processId);

        long address = 0x00600000L;

        long s = apiMemory.readShort(address);
        System.out.println("s = " + s);
        long i = apiMemory.readInt(address);
        System.out.println("i = " + i);
        long l = apiMemory.readLong(address);
        System.out.println("l = " + l);
        float f = apiMemory.readFloat(address);
        System.out.println("f = " + f);
        double d = apiMemory.readDouble(address);
        System.out.println("d = " + d);

        apiMemory.writeInt(address, 1000);

        boolean b = apiMemory.writeByte(address, new int[]{77, 90, 144, 0, 3, 0, 0, 0, 4, 1});

        System.out.println("b = " + b);
    }
}
