package com.dnf.helper;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;

public class Process {

    /**
     * 获取指定进程名的进程id
     *
     * @param processName 进程名称
     * @return int
     */
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

    /**
     * 获取指定进程模块句柄
     *
     * @param processId  进程id
     * @param moduleName 模块名称
     * @return long
     */
    public static long getProcessModuleHandle(int processId, String moduleName) {
        long result = 0;

        WinNT.HANDLE hModuleSnap = Kernel32.INSTANCE.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPMODULE, new WinBase.DWORD(processId));
        Tlhelp32.MODULEENTRY32W me = new Tlhelp32.MODULEENTRY32W();

        if (Kernel32.INSTANCE.Module32FirstW(hModuleSnap, me)) {
            do {
                String currentProcessModuleName = Native.toString(me.szModule).toLowerCase();

                if (currentProcessModuleName.equals(moduleName.toLowerCase())) {
                    WinDef.HMODULE hModule = me.hModule;
                    result = Pointer.nativeValue(hModule.getPointer());
                    break;
                }
            } while (Kernel32.INSTANCE.Module32NextW(hModuleSnap, me));
        }

        Kernel32.INSTANCE.CloseHandle(hModuleSnap);
        return result;
    }
}
