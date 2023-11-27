package com.dnf.config;

import com.dnf.constant.IniConstant;
import com.dnf.driver.ReadWriteMemory;
import com.dnf.driver.api.ApiMemory;
import com.dnf.helper.IniUtils;
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

    /**
     * 辅助配置文件初始化
     *
     * @return IniUtils
     */
    @Bean
    public IniUtils iniUtils() {
        IniUtils iniUtils = new IniUtils();
        iniUtils.setFilename(IniConstant.Helper);
        return iniUtils;
    }
}
