package rocks.alexmihai.cloudflare_ddns.client;

import feign.RequestLine;

public interface IpProviderClient {

    @RequestLine("GET")
    String get();
}
