package dnf.helper;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process {
    public static WinNT.HANDLE openProcess(Integer pid){
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE handle = kernel32.OpenProcess(0x1F0FFF, false, pid);
        if (handle == null){
            int error = kernel32.GetLastError();
            log.error("openProcess::pid = {} , error = {}", pid, error);
            return null;
        }

        return handle;
    }

    public static byte[] readMemory(Integer processId, long address, int size) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE handle = openProcess(processId);
        if (handle == null){
            return null;
        }

        Memory buffer = new Memory(size);
        boolean result = kernel32.ReadProcessMemory(handle, new Pointer(address), buffer, size, null);
        if (!result) {
            int error = kernel32.GetLastError();
            log.error("readMemory:: address = {} , error = {}", address, error);
            return null;
        }

        return buffer.getByteArray(0,size);
    }

    public static boolean writeMemory(Integer processId, long address, byte[] data) {
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE handle = openProcess(processId);
        if (handle == null){
            return false;
        }


        int size = data.length;
        Memory buffer = new Memory(size);
        buffer.write(0, data, 0, size);
        boolean result = kernel32.WriteProcessMemory(handle, new Pointer(address), buffer, size, null);
        if (!result) {
            int error = kernel32.GetLastError();
            log.error("writeMemory:: address = {} , buffer = {} , error = {}", address,buffer, error);
            return false;
        }

        return true;
    }
}
