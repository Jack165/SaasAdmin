package com.feng.boot.admin;

import com.feng.commons.spring.SpringContextUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 入口
 *
 */
@SpringBootApplication
@EnableAsync
public class BootAdminApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BootAdminApplication.class, args);
        SpringContextUtils.setApplicationContext(applicationContext);
    }

}
