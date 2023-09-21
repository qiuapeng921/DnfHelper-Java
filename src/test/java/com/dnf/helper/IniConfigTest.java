package com.dnf.helper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IniConfigTest {

    @Test
    void write() {
        IniUtils iniUtils = new IniUtils();
        iniUtils.setFilename("1111.ini");
        iniUtils.write("刘德华", "年龄", 18);
        iniUtils.write("刘德华", "身高", 174);
        iniUtils.write("刘德华", "体重", 150);
    }
}