package rocks.alexmihai.cloudflare_ddns.properties;

public record CloudflareApi(
        String rootUrl,
        String getDnsRecordsPath,
        String patchRecordPath,
        String token,
        int perPage
) { }
