package com.dnf;

import com.dnf.game.Initialize;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DnfApplication {
    @Resource
    private Initialize game;

    @PostConstruct
    public void Initialize() {
        game.Init();
    }

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DnfApplication.class);
        application.setBannerMode(Banner.Mode.OFF);

        application.run(args);
    }
}