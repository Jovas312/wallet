package com.wallet.service.impl;

import com.wallet.dto.request.CargoRequestApiDTO;
import com.wallet.dto.response.CargoResponseApiDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class ExternalApiGateway {

    private final WebClient webClient;
    private final String merchantId;

    public ExternalApiGateway(@Qualifier("openpayWebClient") WebClient webClient,
                              @Value("${openpay.sandbox.merchant-id}") String merchantId){
        this.webClient = webClient;
        this.merchantId = merchantId;
    }

    public CargoResponseApiDTO procesarCargoTarjeta(CargoRequestApiDTO request) {
        return webClient.post()
                .uri("/{merchantId}/charges", merchantId)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        "Error devuelto por la API de Openpay: " +
                                                clientResponse.statusCode() + " - " + errorBody
                                )))
                )
                .bodyToMono(CargoResponseApiDTO.class)
                .block();
    }
}
