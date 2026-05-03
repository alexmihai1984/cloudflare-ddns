package rocks.alexmihai.cloudflare_ddns;

import lombok.RequiredArgsConstructor;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import rocks.alexmihai.cloudflare_ddns.properties.CloudflareProperties;
import rocks.alexmihai.cloudflare_ddns.properties.HealthcheckProperties;
import rocks.alexmihai.cloudflare_ddns.properties.IpProvidersProperties;
import rocks.alexmihai.cloudflare_ddns.service.ScheduledTaskRunner;

@SpringBootApplication
@RequiredArgsConstructor
@EnableFeignClients
@EnableConfigurationProperties({
		CloudflareProperties.class,
		HealthcheckProperties.class,
		IpProvidersProperties.class
})
@RegisterReflectionForBinding({
		CloudflareProperties.class,
		HealthcheckProperties.class,
		IpProvidersProperties.class
})
public class CloudflareDdnsApplication implements CommandLineRunner {

	private final ScheduledTaskRunner scheduledTaskRunner;

	public static void main(String[] args) {
		SpringApplication.run(CloudflareDdnsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		scheduledTaskRunner.scheduleTask();
	}
}
