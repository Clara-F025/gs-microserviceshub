package com.github.clara_f025.ms_coleta_impacto.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Algoritmo de conversão ambiental ESG. */
@Service
public class ImpactoAmbientalService {

    private static final BigDecimal FATOR_CO2_POR_KG = new BigDecimal("1.5");
    private static final BigDecimal PONTOS_POR_KG = BigDecimal.TEN;

    /** CO2 evitado (kg) = peso (kg) x 1,5. */
    public BigDecimal calcularCo2EvitadoKg(BigDecimal pesoKg) {
        return pesoKg.multiply(FATOR_CO2_POR_KG).setScale(2, RoundingMode.HALF_UP);
    }

    /** Pontos gerados = peso (kg) x 10 (arredondado para inteiro). */
    public long calcularPontos(BigDecimal pesoKg) {
        return pesoKg.multiply(PONTOS_POR_KG).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
