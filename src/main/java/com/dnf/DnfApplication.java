package com.dnf;

import com.dnf.game.Initialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@Slf4j
public class DnfApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DnfApplication.class);
        // 禁止打印banner
        application.setBannerMode(Banner.Mode.OFF);

        ConfigurableApplicationContext applicationContext = application.run(args);

        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("服务关闭");
        }));

        // 从容器获取实例对象
        Initialize initialize = applicationContext.getBean(Initialize.class);
        initialize.init();
    }
}