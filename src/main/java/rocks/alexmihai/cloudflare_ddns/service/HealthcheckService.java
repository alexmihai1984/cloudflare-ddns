package rocks.alexmihai.cloudflare_ddns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.alexmihai.cloudflare_ddns.properties.HealthcheckProperties;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Slf4j
public class HealthcheckService {

    private final HealthcheckProperties healthcheckProperties;
    private final HttpClient httpClient;

    public HealthcheckService(HealthcheckProperties healthcheckProperties) {
        this.healthcheckProperties = healthcheckProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(healthcheckProperties.connectTimeout())
                .build();
    }

    public boolean isOk() {
        if (!healthcheckProperties.enabled()) {
            return false;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(healthcheckProperties.url()))
                    .GET()
                    .timeout(healthcheckProperties.requestTimeout())
                    .build();

            HttpResponse<Void> response = httpClient.send(request, HttpResponse.BodyHandlers.discarding());

            return response.statusCode() == 200;
        } catch (Exception e) {
            log.error("Healthcheck HTTP request failed", e);
            return false;
        }
    }
}
