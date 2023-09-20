package com.dnf.helper;

import lombok.extern.slf4j.Slf4j;
import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;

@Slf4j
public class IniConfig {
    private static volatile IniConfig instance;
    private final Ini ini;
    private String filename;

    private IniConfig() {
        ini = new Ini();
        Config iniConfig = ini.getConfig();
        iniConfig.setEscape(false);
    }

    public static IniConfig getInstance() {
        if (instance == null) {
            synchronized (IniConfig.class) {
                if (instance == null) {
                    instance = new IniConfig();
                }
            }
        }
        return instance;
    }

    public IniConfig setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public void write(String sectionName, String optionName, Object value) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                boolean createRes = file.createNewFile();
                if (!createRes) {
                    throw new Exception("创建文件失败");
                }
            }
            Ini.Section section = ini.get(sectionName);
            if (section == null) {
                section = ini.add(sectionName);
            }
            section.put(optionName, value);
            ini.store(file);
        } catch (Exception e) {
            log.error("写配置失败 section {}，option {}，value {}, error {}", sectionName, optionName, value, e.getMessage());
        }
    }

    public <T> T read(Object sectionName, Object optionName, Class<T> clazz) {
        try {
            File iniFile = new File(filename);
            ini.load(iniFile);
            // 读取配置值
            return ini.get(sectionName, optionName, clazz);
        } catch (Exception e) {
            log.error("读配置失败 section {}，option {}", sectionName, optionName);
            return null;
        }
    }
}
