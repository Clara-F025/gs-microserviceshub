package com.github.clara_f025.ms_gerador_lotes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsGeradorLotesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsGeradorLotesApplication.class, args);
	}

}
