package com.dnf.config;

import com.dnf.driver.ReadWriteMemory;
import com.dnf.driver.impl.ApiMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    /**
     * 初始化读写类
     *
     * @return ReadWriteMemory
     */
    @Bean
    public ReadWriteMemory readWrite() {
        return new ApiMemory();
    }
}
