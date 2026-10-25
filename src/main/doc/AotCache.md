# Java 25 AOT cache in Docker

The Dockerfile builds the executable JAR, extracts Spring Boot layers, and
generates `app.aot` in the final Java 25 runtime image. Training and startup
use the same JVM, `/app` working directory, user, and extracted classpath.

```sh
docker build -t testserver:0.1.0 .
docker run --rm -p 8080:8080 testserver:0.1.0
```

Training uses `-XX:AOTCacheOutput=app.aot` and
`-Dspring.context.exit=onRefresh` to exit after Spring context refresh.
Normal startup uses `-XX:AOTCache=app.aot` without the exit property.
This enables the JVM AOT cache; it does not enable Spring AOT processing
at runtime with `spring.aot.enabled`.

Rebuild the image after changing the application, dependencies, or base JVM.
Do not copy a cache from Windows or another JVM build into the image.
When adding profiles or startup integrations, ensure that the training run
can initialize the application without production credentials or external
services. Changing JVM options can also affect cache compatibility.

To inspect cache loading:

```sh
docker run --rm -e JAVA_TOOL_OPTIONS=-Xlog:aot=info testserver:0.1.0
```

Look for `Opened AOT cache` and `Using AOT-linked classes: true`.
Some classes may be skipped during training; inspect cache-load diagnostics
and measure readiness and first-request latency in the target environment.

Local Windows validation confirmed layered extraction, cache generation,
cache loading, and Spring context initialization on Java 25. Docker image
build and execution have not been tested in this environment.

Reference: [Spring Boot Dockerfiles](https://docs.spring.io/spring-boot/reference/packaging/container-images/dockerfiles.html).
