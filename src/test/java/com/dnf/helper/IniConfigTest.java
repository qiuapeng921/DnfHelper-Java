package com.dnf.helper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IniConfigTest {

    @Test
    void write() {
        IniConfig config = IniConfig.getInstance().setFilename("1111.ini");
        config.write("刘德华", "年龄", 18);
        config.write("刘德华", "身高", 174);
        config.write("刘德华", "体重", 150);
    }
}