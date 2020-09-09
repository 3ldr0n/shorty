# shorty

[![Build Status](https://travis-ci.com/eaneto/shorty.svg?branch=master)](https://travis-ci.com/eaneto/shorty)

A simple URL shortener, built using Java 11 and Redis.

## Local environment

To run shorty you must first build the project using the gradle
wrapper, then run the docker-compose command to run redis.

```bash
./gradlew clean build test
docker build -t shorty:version .
docker-compose up -d
docker run shorty:version
```

**Note**: the project is configured to build using java 11, if you
need to run in an older version you'll need to adapt some parts of the code.

## Endpoints

### Shorten url

```
POST /shorten
```

#### Payload example

```json
{
  "url": "www.github.com/3ldr0n/shorty"
}
```

### Get original url from shortened
```
GET /shorten/{shortenedUrl}
```
