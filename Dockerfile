FROM alpine:3.21.0

COPY ./target/cloudflare-ddns .

ENTRYPOINT ["cloudflare-ddns", "-Xmx128M"]