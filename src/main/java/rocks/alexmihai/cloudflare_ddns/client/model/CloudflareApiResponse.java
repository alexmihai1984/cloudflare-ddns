package rocks.alexmihai.cloudflare_ddns.client.model;

import java.util.List;

public record CloudflareApiResponse<T>(
        boolean success,
        List<CodeMessagePair> errors,
        List<CodeMessagePair> messages,
        T result
) {
}
