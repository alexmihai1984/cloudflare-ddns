package rocks.alexmihai.cloudflare_ddns.properties;

import java.util.Set;

public record CloudflareZone(String id, String token, Set<String> domains) {
}
