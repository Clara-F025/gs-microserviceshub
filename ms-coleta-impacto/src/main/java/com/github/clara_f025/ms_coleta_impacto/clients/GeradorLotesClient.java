package com.github.clara_f025.ms_coleta_impacto.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/** Cliente declarativo do ms-gerador-lotes, resolvido pelo nome no Eureka (sem URL fixa) com Load Balancer. */
@FeignClient(name = "ms-gerador-lotes")
public interface GeradorLotesClient {

    @GetMapping("/api/v1/lotes/disponiveis")
    List<LoteRemotoResponse> listarDisponiveis(@RequestParam(value = "cep", required = false) String cep);

    @PutMapping("/api/v1/lotes/{protocolo}/reservar")
    LoteRemotoResponse reservar(@PathVariable("protocolo") String protocolo,
                                @RequestBody ReservarLoteRemoteRequest request);

    @PutMapping("/api/v1/lotes/{protocolo}/liberar")
    LoteRemotoResponse liberar(@PathVariable("protocolo") String protocolo);

    @PutMapping("/api/v1/lotes/{protocolo}/coletar")
    LoteRemotoResponse coletar(@PathVariable("protocolo") String protocolo,
                               @RequestBody ColetarLoteRemoteRequest request);
}
