package com.dnf.game;

import com.dnf.driver.ReadWriteMemory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Base {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected ReadWriteMemory memory;
}
