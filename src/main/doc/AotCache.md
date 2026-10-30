# Container builds and AOT scope

Container images are built with Spring Boot Cloud Native Buildpacks. Jib and
Dockerfile-based image builds have been removed.

## Build and run

Install JDK 25 and run a Docker daemon with Linux container support:

```sh
./gradlew bootBuildImage
docker run --rm -p 127.0.0.1:8080:8080 -p 127.0.0.1:8081:8081 testserver/testserver:0.1.0
```

On Windows, use `.\gradlew.bat bootBuildImage`.
The task uses the Paketo Noble Java Tiny builder and requests Java 25 explicitly.
`BP_NATIVE_IMAGE=false` selects a JVM image even when GraalVM build tooling is present.
The builder supplies the runtime and non-root launch user; no custom Dockerfile is needed.
Building requires network access to fetch builder/run images and buildpack dependencies.
The builder's `latest` tag receives updates, so rebuilds may use newer builder contents.

Override the image name when needed:

```sh
./gradlew bootBuildImage --imageName=testserver:smoke
bash .github/scripts/check-container.sh
```

The smoke test builds and runs the image, checks health and core API responses,
and removes its temporary container. It does not send real Firebase notifications.

## AOT scope

The previous Dockerfile explicitly trained and loaded a Java 25 JVM AOT cache.
That custom training step is no longer configured. Do not assume that the new image
loads the previous `app.aot` cache or preserves its startup performance.
Spring AOT processing and GraalVM native-image compilation are separate mechanisms;
this configuration requests a JVM image, not a native executable.
Any future AOT optimization must be configured and measured with the selected buildpack.

Reference: [Spring Boot OCI image packaging](https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html).
