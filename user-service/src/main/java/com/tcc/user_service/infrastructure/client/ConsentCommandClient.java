package com.tcc.user_service.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class ConsentCommandClient {

    private final RestClient restClient;
    private final RestClient tokenClient;
    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;

    public ConsentCommandClient(
            @Value("${consent-command.url}") String consentCommandUrl,
            @Value("${keycloak.token-uri}") String tokenUrl,
            @Value("${keycloak.client-id}") String clientId,
            @Value("${keycloak.client-secret}") String clientSecret) {
        this.restClient = RestClient.builder()
                .baseUrl(consentCommandUrl)
                .build();
        this.tokenClient = RestClient.builder().build();
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private String getAccessToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);

        var response = tokenClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toEntity(Map.class);

        Map<String, Object> tokenResponse = response.getBody();
        if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
            throw new RuntimeException("Failed to obtain access token from Keycloak");
        }
        return (String) tokenResponse.get("access_token");
    }

    public DeactivateResult deactivate(Long ownerId, String issuerType, Long issuerId) {
        try {
            String token = getAccessToken();
            var response = restClient.post()
                    .uri("/api/v1/consent/deactivate")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of(
                            "ownerId", ownerId,
                            "issuedBy", Map.of(
                                    "issuer", issuerType,
                                    "id", issuerId
                            )
                    ))
                    .retrieve()
                    .toEntity(Map.class);

            Map<String, Object> body = response.getBody();
            int consentsRevoked = body != null ? (int) body.get("consentsRevoked") : 0;
            return new DeactivateResult(true, consentsRevoked);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException(
                        "No consent records found for user " + ownerId + " in consent service.", e);
            }
            throw new RuntimeException(
                    "Consent service returned error for user " + ownerId + ": " + e.getStatusCode(), e);
        } catch (HttpServerErrorException | ResourceAccessException e) {
            throw new RuntimeException(
                    "Consent service is unavailable. Failed to deactivate user " + ownerId + ".", e);
        }
    }

    public record DeactivateResult(boolean success, int consentsRevoked) {}
}
