package rocks.alexmihai.cloudflare_ddns.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;

@ConfigurationProperties(prefix = "healthcheck")
public record HealthcheckProperties(
        boolean enabled,
        String url,
        @DefaultValue("10s") Duration connectTimeout,
        @DefaultValue("10s") Duration requestTimeout
) {
}
