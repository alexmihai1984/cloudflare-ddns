package rocks.alexmihai.cloudflare_ddns.client;

import feign.Feign;
import feign.Logger;
import feign.okhttp.OkHttpClient;
import org.springframework.stereotype.Component;
import rocks.alexmihai.cloudflare_ddns.properties.IpProvidersProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class IpProviderClientFactory {

    private final Map<String, IpProviderClient> clients;
    private final IpProvidersProperties ipProvidersProperties;

    public IpProviderClientFactory(IpProvidersProperties ipProvidersProperties) {
        this.ipProvidersProperties = ipProvidersProperties;
        this.clients = new LinkedHashMap<>();

        for (IpProvidersProperties.IpProvider provider : ipProvidersProperties.providers()) {
            clients.put(provider.name(), buildClient(provider.url()));
        }
    }

    private IpProviderClient buildClient(String url) {
        return Feign.builder()
                .client(new OkHttpClient())
                .logger(new Logger.JavaLogger(IpProviderClient.class.getName()))
                .logLevel(Logger.Level.BASIC)
                .target(IpProviderClient.class, url);
    }

    public Map<IpProvidersProperties.IpProvider, IpProviderClient> providers() {
        Map<IpProvidersProperties.IpProvider, IpProviderClient> res = new LinkedHashMap<>();

        for (IpProvidersProperties.IpProvider p : ipProvidersProperties.providers()) {
            res.put(p, clients.get(p.name()));
        }

        return res;
    }
}
