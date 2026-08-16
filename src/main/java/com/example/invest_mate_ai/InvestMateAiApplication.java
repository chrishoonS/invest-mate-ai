package com.example.invest_mate_ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.invest_mate_ai")
public class InvestMateAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(InvestMateAiApplication.class, args);
	}

}
