package rocks.alexmihai.cloudflare_ddns.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ScheduledTaskRunner {

    @Value("${scheduler.interval.seconds:60}")
    private final int intervalSecs;

    private final CloudflareService cloudflareService;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

    public ScheduledTaskRunner(
            @Value("${scheduler.interval.seconds:60}") int intervalSecs,
            CloudflareService cloudflareService
    ) {
        this.intervalSecs = intervalSecs;
        this.cloudflareService = cloudflareService;
    }

    public void scheduleTask() {
        log.info("Scheduling task every {}s", intervalSecs);
        executorService.scheduleWithFixedDelay(this::runTask, 0, intervalSecs, TimeUnit.SECONDS);
    }

    public void runTask() {
        log.info("Running task");
        try {
            cloudflareService.updateIp();
        } catch (Exception e) {
            log.error("Failed to run task", e);
        }
    }
}
