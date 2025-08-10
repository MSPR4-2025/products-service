# Products Service

## Docker

Deploy service with database (will pull the latest image from the GitHub registry)

```shell
docker compose up
```

To build your own docker image you can do:

```shell
./gradlew bootBuildImage --imageName=ghcr.io/mspr4-2025/products-service
```

## OpenAPI

The OpenAPI v3 documentation is available at [/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

## SonarQube

You can find the SonarQube Scan results on [SonarCloud](https://sonarcloud.io/project/overview?id=MSPR4-2025_products-service).
