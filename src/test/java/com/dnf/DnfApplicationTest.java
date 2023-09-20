package com.dnf;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;

@SpringBootTest
class DnfApplicationTest {

    @Test
    public void iniTest() {
        try {
            // 创建 Ini 对象并加载 INI 文件
            Ini ini = new Ini();
            Config iniConfig = ini.getConfig();
            iniConfig.setEscape(false);
            File iniFile = new File("helper.ini");
            ini.load(iniFile);

            // 读取配置值
            String value = ini.get("自动配置", "技能代码");
            System.out.println(value);

            // 修改配置值
            ini.put("自动配置", "技能代码", 70231);

           
            FileOutputStream fos = new FileOutputStream(iniFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            PrintWriter writer = new PrintWriter(osw);
            ini.store(writer);
            writer.close();

            // 读取配置值
            value = ini.get("自动配置", "技能代码");
            System.out.println(value);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}