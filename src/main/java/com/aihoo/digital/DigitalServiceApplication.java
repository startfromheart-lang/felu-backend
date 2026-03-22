package com.aihoo.digital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aihoo.digital.mapper")
public class DigitalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DigitalServiceApplication.class, args);
    }
}
