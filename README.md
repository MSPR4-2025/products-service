# Products Service

## Docker

Create docker image

```shell
./gradlew bootBuildImage --imageName=mspr4-2025/products-service
```

Deploy service with database

```shell
docker compose up
```

## OpenAPI

The OpenAPI v3 documentation is available at [/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)