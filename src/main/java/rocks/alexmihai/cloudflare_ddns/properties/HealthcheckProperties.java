package rocks.alexmihai.cloudflare_ddns.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "healthcheck")
public record HealthcheckProperties(boolean enabled, String url) {
}
