package rocks.alexmihai.cloudflare_ddns.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;

@ConfigurationProperties(prefix = "cloudflare")
public record CloudflareProperties(
        CloudflareApi api,
        List<CloudflareZone> zones
) {

    public record CloudflareApi(
            String rootUrl,
            String getDnsRecordsPath,
            String patchRecordPath,
            String token,
            int perPage
    ) { }

    public record CloudflareZone(String id, String token, Set<String> domains) {
    }
}
