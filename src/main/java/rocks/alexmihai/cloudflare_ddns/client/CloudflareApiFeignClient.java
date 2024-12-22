package rocks.alexmihai.cloudflare_ddns.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import rocks.alexmihai.cloudflare_ddns.client.model.CloudflareApiResponse;
import rocks.alexmihai.cloudflare_ddns.client.model.CloudflareDnsResult;
import rocks.alexmihai.cloudflare_ddns.client.model.DnsRecordUpdate;

import java.util.List;

@FeignClient(
        value = "cloudflareApiFeignClient",
        url = "${cloudflare.api.root_url:https://api.cloudflare.com/client/v4}"
)
public interface CloudflareApiFeignClient {

    @GetMapping("${cloudflare.api.get_dns_records_path:/zones/{zone_id}/dns_records}")
    CloudflareApiResponse<List<CloudflareDnsResult>> getDnsRecords(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("zone_id") String zoneId,
            @RequestParam("per_page") int perPage
    );

    @PatchMapping("${cloudflare.api.patch_record_path:/zones/{zone_id}/dns_records/{dns_record_id}}")
    CloudflareApiResponse<CloudflareDnsResult> updateIp(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable("zone_id") String zoneId,
            @PathVariable("dns_record_id") String dnsRecordId,
            @RequestBody DnsRecordUpdate dnsRecordUpdate
    );
}
