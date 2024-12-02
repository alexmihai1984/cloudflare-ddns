package rocks.alexmihai.cloudflare_ddns.client.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CloudflareDnsResult(
        String comment,
        String name,
        int ttl,
        String content,
        String type,
        @JsonProperty("created_on") String createdOn,
        String id,
        @JsonProperty("modified_on") String modifiedOn
) {
}
