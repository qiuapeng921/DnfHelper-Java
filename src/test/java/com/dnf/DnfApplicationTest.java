package com.dnf;

import org.ini4j.Ini;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@SpringBootTest
class DnfApplicationTest {

    @Test
    public void iniTest() {
        try {
            // 创建 Ini 对象并加载 INI 文件
            Ini ini = new Ini();
            ini.getConfig().setFileEncoding(StandardCharsets.UTF_8);

            File iniFile = new File("helper.ini");
            ini.load(iniFile);

            // 读取配置值
            String value = ini.get("自动配置", "技能代码");
            System.out.println(value);

            // 修改配置值
            ini.put("自动配置", "技能代码", 70231);

            // 使用 PrintWriter 写入修改后的 INI 文件，并指定文件编码为 UTF-8
            FileOutputStream fos = new FileOutputStream(iniFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            PrintWriter writer = new PrintWriter(osw);
            ini.store(writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}