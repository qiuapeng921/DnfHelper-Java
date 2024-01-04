package com.dnf.game;

import com.dnf.driver.ReadWriteMemory;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 情歌
 */
public class Base {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected ReadWriteMemory memory;
}
