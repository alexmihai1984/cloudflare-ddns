package rocks.alexmihai.cloudflare_ddns.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "cloudflareDnsTraceFeignClient",
        url = "${cloudflare.dns_trace.url:https://1.1.1.1/cdn-cgi/trace}"
)
public interface CloudflareDnsTraceFeignClient {

    @GetMapping
    String getTrace();
}
