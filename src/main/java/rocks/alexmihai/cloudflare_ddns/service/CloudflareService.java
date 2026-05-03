package rocks.alexmihai.cloudflare_ddns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.alexmihai.cloudflare_ddns.client.CloudflareApiFeignClient;
import rocks.alexmihai.cloudflare_ddns.client.model.CloudflareDnsResult;
import rocks.alexmihai.cloudflare_ddns.client.model.DnsRecordUpdate;
import rocks.alexmihai.cloudflare_ddns.properties.CloudflareProperties;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareService {

    private final CloudflareProperties cloudflareProperties;
    private final CloudflareApiFeignClient cloudflareApiFeignClient;
    private final IpConsensusFetcher ipConsensusFetcher;
    private final HealthcheckService healthcheckService;

    void updateIp() {
        if (healthcheckService.isOk()) {
            log.info("Healthcheck successful, not doing anything");
            return;
        }

        var ip = ipConsensusFetcher.fetchIp();
        log.info("Determined public IP is '{}'", ip);

        for (var zone : cloudflareProperties.zones()) {
            updateZone(zone, ip);
        }
    }

    private void updateZone(CloudflareProperties.CloudflareZone zone, String ip) {
        var token = getAuthToken(zone);

        var apiResponse = cloudflareApiFeignClient.getDnsRecords(
                "Bearer " + token,
                zone.id(),
                cloudflareProperties.api().perPage()
        );

        for (var dnsRecord : apiResponse.result()) {
            updateDnsRecord(zone, ip, dnsRecord, token);
        }
    }

    private void updateDnsRecord(
            CloudflareProperties.CloudflareZone zone,
            String ip,
            CloudflareDnsResult dnsRecord,
            String token
    ) {
        if (!zone.domains().contains(dnsRecord.name())) {
            return;
        }

        if (!"A".equals(dnsRecord.type())) {
            return;
        }

        if (ip.equals(dnsRecord.content())) {
            log.info("IP for '{}' up to date ('{}')", dnsRecord.name(), ip);
            return;
        }

        var response = cloudflareApiFeignClient.updateIp(
                "Bearer " + token,
                zone.id(),
                dnsRecord.id(),
                new DnsRecordUpdate(ip)
        );
        log.info("Updated '{}' to IP '{}', Response: '{}'", dnsRecord.name(), ip, response);
    }

    private String getAuthToken(CloudflareProperties.CloudflareZone zone) {
        if (zone.token() != null) {
            log.info("Using token override for zone '{}'", zone.id());
            return zone.token();
        }

        return cloudflareProperties.api().token();
    }
}
