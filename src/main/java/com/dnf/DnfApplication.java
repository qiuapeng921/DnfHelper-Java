package com.dnf;

import com.dnf.game.Initialize;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DnfApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DnfApplication.class);
        // 禁止打印banner
        application.setBannerMode(Banner.Mode.OFF);

        ConfigurableApplicationContext applicationContext = application.run(args);

        // 从容器获取实例对象
        Initialize initialize = applicationContext.getBean(Initialize.class);
        initialize.Init();
    }
}