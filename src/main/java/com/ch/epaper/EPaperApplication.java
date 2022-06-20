package com.ch.epaper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.ch.epaper.mapper")
public class EPaperApplication {
    public static void main(String[] args) {
        SpringApplication.run(EPaperApplication.class, args);
    }

}
