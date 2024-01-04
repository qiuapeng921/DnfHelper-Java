package com.dnf.helper;

import lombok.extern.slf4j.Slf4j;
import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;

/**
 * @author 情歌
 */
@Slf4j
public class IniUtils {
    private final Ini ini;
    private String filename;

    public IniUtils() {
        ini = new Ini();
        Config iniConfig = ini.getConfig();
        iniConfig.setEscape(false);
    }


    public IniUtils setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    public void write(String sectionName, String optionName, Object value) {
        try {
            File file = new File(filename);
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
