package rocks.alexmihai.cloudflare_ddns.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "cloudflareDnsBackupTraceFeignClient",
        url = "${cloudflare.dns_trace.url:https://1.0.0.1/cdn-cgi/trace}"
)
public interface CloudflareDnsBackupTraceFeignClient {

    @GetMapping
    String getTrace();
}
