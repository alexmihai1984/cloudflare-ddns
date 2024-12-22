package rocks.alexmihai.cloudflare_ddns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.alexmihai.cloudflare_ddns.client.CloudflareApiFeignClient;
import rocks.alexmihai.cloudflare_ddns.client.CloudflareDnsBackupTraceFeignClient;
import rocks.alexmihai.cloudflare_ddns.client.CloudflareDnsTraceFeignClient;
import rocks.alexmihai.cloudflare_ddns.client.model.CloudflareDnsResult;
import rocks.alexmihai.cloudflare_ddns.client.model.DnsRecordUpdate;
import rocks.alexmihai.cloudflare_ddns.properties.CloudflareProperties;
import rocks.alexmihai.cloudflare_ddns.properties.CloudflareZone;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudflareService {

    private final CloudflareProperties cloudflareProperties;
    private final CloudflareApiFeignClient cloudflareApiFeignClient;
    private final CloudflareDnsTraceFeignClient cloudflareDnsTraceFeignClient;
    private final CloudflareDnsBackupTraceFeignClient cloudflareDnsBackupTraceFeignClient;

    void updateIp() throws Exception {
        var ip = retrievePublicIp();
        log.info("Determined public IP is '{}'", ip);

        for (var zone : cloudflareProperties.zones()) {
            updateZone(zone, ip);
        }
    }

    private void updateZone(CloudflareZone zone, String ip) {
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

    private void updateDnsRecord(CloudflareZone zone, String ip, CloudflareDnsResult dnsRecord, String token) {
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

    private String retrievePublicIp() throws IOException {
        try {
            return retrievePublicIp(cloudflareDnsTraceFeignClient::getTrace);
        } catch (Exception e) {
            log.warn("Failed to retrieve public IP from main URL", e);
            return retrievePublicIp(cloudflareDnsBackupTraceFeignClient::getTrace);
        }
    }

    private String retrievePublicIp(Supplier<String> traceRetriever) throws IOException {
        String response = traceRetriever.get();
        log.debug("Trace response: {}", response);

        Properties properties = new Properties();
        properties.load(new StringReader(response));

        return properties.getProperty("ip");
    }

    private String getAuthToken(CloudflareZone zone) {
        if (zone.token() != null) {
            log.info("Using token override for zone '{}'", zone.id());
            return zone.token();
        }

        return cloudflareProperties.api().token();
    }
}
