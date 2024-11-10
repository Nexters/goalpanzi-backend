package com.nexters.goalpanzi;

import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class GoalpanziApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(GoalpanziApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        var serverPort = ctx.getEnvironment().getProperty("server.port");
        var profile = ctx.getEnvironment().getProperty("spring.profiles.active");

        // startup notification
        String banner = String.format("GoalpanziApplication started (profile=%s, port=%s)", profile, serverPort);
        System.out.println(banner);
        LoggerFactory.getLogger("ServerBoot").info(banner);
    }
}
