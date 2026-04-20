package rocks.alexmihai.cloudflare_ddns.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "ip-providers")
public record IpProvidersProperties(List<IpProvider> providers) {

    public record IpProvider(String name, String url, String type) {
    }
}
