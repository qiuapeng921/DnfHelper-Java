package dnf;

import dnf.helper.Bytes;
import dnf.helper.Process;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {
    public static void main(String[] args) {
        byte[] bytes1 = Bytes.intToByteArray(123);
        log.info("main::args = {}", bytes1);
        byte[] bytes = Process.readMemory(6976,64562121L, 10);
        log.info("main::args = {}", bytes);
    }
}