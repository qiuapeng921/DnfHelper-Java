package com.dnf.game;

import cn.hutool.core.date.DateUtil;
import com.dnf.helper.Timer;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Screen {
    Logger logger = LoggerFactory.getLogger(Screen.class.getName());

    @Resource
    private GameCall gameCall;

    private boolean screenSwitch;

    public void screenSwitch() {
        if (!screenSwitch) {
            Thread thread = new Thread(this::screenThread);
            thread.start();
            screenSwitch = true;
            logger.info("技能全屏 - [ √ ]");
        } else {
            screenSwitch = false;
            logger.info("技能全屏 - [ x ]");
        }
    }

    private void screenThread() {
        while (screenSwitch) {
            Timer.sleep(300);
            fullScreen();
        }
    }


    private void fullScreen() {
        logger.info("全屏遍历：{}", DateUtil.date(System.currentTimeMillis()));
    }


    public void screenKill() {
        gameCall.skillCall(0, 54141, 0, 0, 0, 0, 1.0F);
        logger.info("秒杀完毕 [ √ ]");
    }
}
