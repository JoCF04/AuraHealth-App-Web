package com.AuraHealth.api.auraservices;

import com.AuraHealth.api.auradtos.OpenFdaInfoDTO;
import com.AuraHealth.api.auradtos.OpenFdaWarningsDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class OpenFdaService {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "https://api.fda.gov/drug/label.json";

    public OpenFdaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // ── HU37 — Información del medicamento ───────────────────────────────────

    public OpenFdaInfoDTO obtenerInfo(String nombre) {
        Map<String, Object> resultado = buscarEnOpenFda(nombre);
        if (resultado == null) {
            return new OpenFdaInfoDTO(false, List.of(), List.of(), List.of());
        }
        return new OpenFdaInfoDTO(
            true,
            extraerCampo(resultado, "indications_and_usage"),
            extraerCampo(resultado, "dosage_and_administration"),
            extraerCampo(resultado, "purpose")
        );
    }

    // ── HU40 — Advertencias del medicamento ──────────────────────────────────

    public OpenFdaWarningsDTO obtenerWarnings(String nombre) {
        Map<String, Object> resultado = buscarEnOpenFda(nombre);
        if (resultado == null) {
            return new OpenFdaWarningsDTO(false, List.of(), List.of(), List.of());
        }
        return new OpenFdaWarningsDTO(
            true,
            extraerCampo(resultado, "warnings"),
            extraerCampo(resultado, "do_not_use"),
            extraerCampo(resultado, "ask_doctor")
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> buscarEnOpenFda(String nombre) {
        Map<String, Object> resultado = llamarOpenFda("openfda.generic_name", nombre);
        if (resultado == null) {
            resultado = llamarOpenFda("openfda.brand_name", nombre);
        }
        return resultado;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> llamarOpenFda(String campo, String nombre) {
        try {
            URI uri = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("search", campo + ":\"" + nombre + "\"")
                .queryParam("limit", 1)
                .build()
                .encode()
                .toUri();

            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);
            List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.getBody().get("results");

            if (results != null && !results.isEmpty()) {
                return results.get(0);
            }
            return null;

        } catch (HttpClientErrorException.NotFound e) {
            // openFDA devuelve 404 cuando no hay match — es comportamiento normal
            return null;
        } catch (RestClientException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> extraerCampo(Map<String, Object> resultado, String campo) {
        Object valor = resultado.get(campo);
        if (valor instanceof List) {
            return (List<String>) valor;
        }
        return List.of();
    }
}
