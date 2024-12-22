package rocks.alexmihai.cloudflare_ddns.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "cloudflare")
public record CloudflareProperties(
        CloudflareDnsTrace dnsTrace,
        CloudflareApi api,
        List<CloudflareZone> zones
) {
}
