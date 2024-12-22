FROM ubuntu:25.04

COPY ./target/cloudflare-ddns .

ENTRYPOINT ["./cloudflare-ddns", "-Xmx128M"]