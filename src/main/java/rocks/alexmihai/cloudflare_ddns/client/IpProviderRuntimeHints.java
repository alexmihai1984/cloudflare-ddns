package rocks.alexmihai.cloudflare_ddns.client;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class IpProviderRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.proxies().registerJdkProxy(IpProviderClient.class);
        hints.reflection().registerType(IpProviderClient.class, MemberCategory.INVOKE_PUBLIC_METHODS);
    }
}
