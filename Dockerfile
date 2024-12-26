# Stage 1: Build for amd64
FROM ubuntu:25.04 AS builder-amd64
COPY ./target/cloudflare-ddns-amd64 /app/cloudflare-ddns
RUN chmod +x /app/cloudflare-ddns

# Stage 2: Build for arm64
FROM ubuntu:25.04 AS builder-arm64
COPY ./target/cloudflare-ddns-arm64 /app/cloudflare-ddns
RUN chmod +x /app/cloudflare-ddns

# Stage 3: Create the final image
FROM ubuntu:25.04 AS final
COPY --from=builder-${TARGETARCH} /app/cloudflare-ddns ./cloudflare-ddns
ENTRYPOINT ["./cloudflare-ddns", "-Xmx128M"]
