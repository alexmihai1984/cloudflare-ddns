# About

This project is meant to help people that self-host some application and use 
Cloudflare for DNS. Given that it is common for internet providers to offer dynamic 
IPs, you will need to keep your DNS records up to date with your current public IP.

# How it works

You can run this project in your network, and it will keep your DNS records up to date.
Technically, what it does is:

- it runs a scheduled task every 60s (this is configurable, but I consider this a 
sensible default)
- it queries https://1.1.1.1/cdn-cgi/trace or https://1.0.0.1/cdn-cgi/trace (as fallback) 
to get the public IP
- for each configured Cloudflare zone, it queries the Cloudflare API for that zone's 
DNS records (https://developers.cloudflare.com/api/operations/dns-records-for-a-zone-list-dns-records)
- for each record of type 'A' matching a configured domain/subdomain, if the DNS record 
doesn't match the current public IP, it updates it by performing a PATCH request 
to the Cloudflare API (https://developers.cloudflare.com/api/operations/dns-records-for-a-zone-patch-dns-record)

# Configuration

The configuration is based on an `application.yml` file. For those familiar with Spring Boot, this should look very 
familiar. You can find below `application.yml` examples. 

## Single domain

```yaml
cloudflare:
  api:
    token: <your cloudflare api token>
  zones:
    - id: <your cloudflare zone id>
      domains:
        - yourdomain.com
```

## Domain with subdomains

```yaml
cloudflare:
  api:
    token: <your cloudflare api token>
  zones:
    - id: <your cloudflare zone id>
      domains:
        - yourdomain.com
        - api.yourdomain.com
        - static.yourdomain.com
```

## Multiple domains

```yaml
cloudflare:
  api:
    token: <your cloudflare api token>
  zones:
    - id: <your cloudflare zone 1 id>
      domains:
        - yourdomain-1.com
    - id: <your cloudflare zone 2 id>
      domains:
        - yourdomain-2.com
    - id: <your cloudflare zone 3 id>
      domains:
        - yourdomain-3.com
```

## Distinct API tokens

In case you want to use different Cloudflare API tokens for different zones, you can set one token per zone. 

```yaml
cloudflare:
  zones:
    - id: <your cloudflare zone 1 id>
      token: <your cloudflare api token for zone 1>
      domains:
        - yourdomain-1.com
    - id: <your cloudflare zone 2 id>
      token: <your cloudflare api token for zone 2>
      domains:
        - yourdomain-2.com
    - id: <your cloudflare zone 3 id>
      token: <your cloudflare api token for zone 3>
      domains:
        - yourdomain-3.com
```

## Change scheduled task frequency

If you want the update to run more or less frequent, you can do it by setting the `scheduler.interval.seconds` property.
Below you see an example of how to run it every 5 minutes.

```yaml
scheduler.interval.seconds: 300
cloudflare:
  api:
    token: <your cloudflare api token>
  zones:
    - id: <your cloudflare zone id>
      domains:
        - yourdomain.com
```

# Running it

Have your `application.yml` configuration file somewhere on disk and choose one of the options below.

## Docker

```shell
docker run -d \
  --name cloudflare-ddns \
  --restart unless-stopped \
  -v /path/to/application.yml:/application.yml \
  alexmihai1984/cloudflare-ddns:latest
```

## Docker Compose

```yaml
services:
  cloudflare-ddns:
    image: alexmihai1984/cloudflare-ddns:latest
    container_name: cloudflare-ddns
    restart: unless-stopped
    volumes:
      - /path/to/application.yml:/application.yml

networks:
  cloudflare-ddns-network:
    name: cloudflare-ddns-network
```

## Standalone

You either download the jar file from the Github Packages or build it from sources. You will need JDK 21 to build it:

```shell
mvn clean install
```

Then run it:

```shell
java -jar target/cloudflare-ddns-3.0.0.jar --spring.config.additional-location=/path/to/application.yml
```

# Logs

By default, the application keeps logs in the `/var/log/cloudflare-ddns` folder. It keeps 10 files of log history 
100MB each, so you can accumulate ~1GB of logs. All of this is configurable, and you can change it by adding one or more 
of these config properties to your `application.yml` file, with whatever values fit your needs.

```yaml
logging:
  file.name: /var/log/cloudflare-ddns/cloudflare-ddns.log
  logback.rollingpolicy:
    max-file-size: 100MB
    max-history: 10
```

# Implementation

The application is written in Java as a Spring Boot console application. 
For this reason you can expect ~200MB of RAM usage, mostly for the JVM itself, if you run it standalone.
Other than that, the application itself should have minimal CPU and RAM consumption.

However, if you use the provided Docker image, it uses a GraalVM native image, which reduces the overall 
memory consumption to about 50MB.

# Alternative

I suggest you also look at this project:
https://github.com/timothymiller/cloudflare-ddns . It's the project I got inspired by, and it's more flexible. 
For example, my solution only supports IPV4, not IPv6. I just needed something minimal but with some tweaks.

The reasons why I wrote my own are:
- sometimes on my setup, when powered up, @timothymiller's solution would simply not work; 
I'm not exactly sure why and that gets me to the second point 
- lack of logs of what went wrong in the above scenario and lack of timestamps on the logs
- unnecessary configuration; I just wanted to change the IP, I don't see why I'd want to change `ttl` for example; 
and by looking at the code it seemed to me that `ttl` was both used for the DNS TTL and for the running frequency 
of the task. 

Initially I wanted to contribute to @timothymiller's solution, but I wanted something fast and I would have needed to 
get familiar with Python, which I'm really not, and that would have been much more time-consuming. 
And since I did it I thought I'd make it publicly, freely available.
