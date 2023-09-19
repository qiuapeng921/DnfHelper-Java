package com.dnf.helper;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Tlhelp32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;

public class Process {
    public static int getProcessId(String processName) {
        Tlhelp32.PROCESSENTRY32.ByReference pe = new Tlhelp32.PROCESSENTRY32.ByReference();

        // 创建进程快照
        WinNT.HANDLE handle = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPPROCESS, new WinBase.DWORD(0));
        if (handle == WinBase.INVALID_HANDLE_VALUE) {
            return 0;
        }

        try {
            // 遍历进程快照
            if (Kernel32.INSTANCE.Process32First(handle, pe)) {
                do {
                    String exeFile = Native.toString(pe.szExeFile);
                    if (exeFile.equalsIgnoreCase(processName.toUpperCase())) {
                        return pe.th32ProcessID.intValue();
                    }
                } while (Kernel32.INSTANCE.Process32Next(handle, pe));
            }
        } finally {
            Kernel32.INSTANCE.CloseHandle(handle);
        }

        return 0;
    }
}
