package com.dnf.game;

import com.dnf.driver.impl.ApiMemory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Initialize {
    private final Logger logger = LoggerFactory.getLogger(Initialize.class.getName());

    @Resource
    private ApiMemory apiMemory;


    public void Init() {
        logger.info("程序初始化");
        rw();
    }


    public void rw() {
        int processId = 14000;
        long address = 0x7FF738E70000L;

        apiMemory.setProcessId(processId);

        int[] intArr = apiMemory.readByteMemory(address, 10);
        logger.info("args = {}", intArr);


        int i = apiMemory.readInt(address);
        logger.info("args = {}", i);
    }
}
