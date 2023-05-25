package dnf.helper;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Process {

    public static WinNT.HANDLE openProcess(Integer pid){
        Kernel32 kernel32 = Kernel32.INSTANCE;
        WinNT.HANDLE process = kernel32.OpenProcess(0x1F0FFF, false, pid);
        if (process == null) {
            int error = kernel32.GetLastError();
            log.error("openProcess::pid = {} , error = {}", pid, error);
        } else {
            log.info("Process opened successfully = {}", pid);
            kernel32.CloseHandle(process);
        }
        return process;
    }

}
