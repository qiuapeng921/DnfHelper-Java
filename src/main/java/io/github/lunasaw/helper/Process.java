package io.github.lunasaw.helper;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class Process {

    public static WinNT.HANDLE openProcess(Integer pid){
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE process = kernel32.OpenProcess(0x1F0FFF, false, pid);
        if (process == null) {
            int error = kernel32.GetLastError();
            System.out.println("Failed to open process. Error code: " + error);
        } else {
            System.out.println("Process opened successfully");
            kernel32.CloseHandle(process);
        }
        return process;
    }

}
