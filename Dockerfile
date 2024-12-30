# amd64
FROM ubuntu:25.04 AS build-amd64
COPY ./target/cloudflare-ddns-amd64 ./cloudflare-ddns

# arm64
FROM ubuntu:25.04 AS build-arm64
COPY ./target/cloudflare-ddns-arm64 ./cloudflare-ddns

# common
FROM build-${TARGETARCH} AS build
ENTRYPOINT ["./cloudflare-ddns", "-Xmx128M"]
