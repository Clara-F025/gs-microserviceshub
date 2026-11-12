package com.github.clara_f025.ms_coleta_impacto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsColetaImpactoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsColetaImpactoApplication.class, args);
	}

}
