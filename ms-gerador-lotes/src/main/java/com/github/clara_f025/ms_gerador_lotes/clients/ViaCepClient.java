package com.github.clara_f025.ms_gerador_lotes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** API pública externa do ViaCEP (a URL base é externalizada em application.properties). */
@FeignClient(name = "viacep", url = "${viacep.url}")
public interface ViaCepClient {

    @GetMapping("/{cep}/json/")
    ViaCepResponse buscarPorCep(@PathVariable("cep") String cep);
}
