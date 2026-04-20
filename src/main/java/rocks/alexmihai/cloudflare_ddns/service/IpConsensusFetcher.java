package rocks.alexmihai.cloudflare_ddns.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.alexmihai.cloudflare_ddns.client.IpProviderClient;
import rocks.alexmihai.cloudflare_ddns.client.IpProviderClientFactory;
import rocks.alexmihai.cloudflare_ddns.properties.IpProvidersProperties;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class IpConsensusFetcher {

    private final IpProviderClientFactory ipProviderClientFactory;

    public String fetchIp() {
        Map<IpProvidersProperties.IpProvider, IpProviderClient> providers = ipProviderClientFactory.providers();

        var countByIp = new HashMap<String, Integer>();
        for (IpProvidersProperties.IpProvider provider : providers.keySet()) {
            IpProviderClient client = providers.get(provider);
            try {
                String ip = fetchIpFromProvider(provider, client);
                log.info("'{}' voted '{}'", provider.url(), ip);

                countByIp.putIfAbsent(ip, 1);
                countByIp.put(ip, countByIp.get(ip) + 1);
            } catch (Exception ex) {
                log.warn("Error calling '{}'", provider.url(), ex);
            }
        }

        String res = "";
        int max = 0;
        for (String ip : countByIp.keySet()) {
            int count = countByIp.get(ip);
            if (count > max) {
                max = count;
                res = ip;
            }
        }

        log.info("Consensus was IP was '{}'", res);
        return res;
    }

    private String fetchIpFromProvider(IpProvidersProperties.IpProvider properties, IpProviderClient client) throws IOException {
        String response = client.get();
        log.debug("Raw response from provider '{}': {}", properties.name(), response);

        if ("cloudflare_trace".equals(properties.type())) {
            Properties props = new Properties();
            props.load(new StringReader(response));
            return props.getProperty("ip");
        }

        // plain_text: response is just the IP (possibly with trailing newline)
        return response.trim();
    }
}
