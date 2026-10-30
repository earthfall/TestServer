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

## JVM AOT cache

`bootBuildImage` sets `BP_JVM_AOTCACHE_ENABLED=true` alongside Java 25.
The Paketo Spring Boot buildpack trains the application during image creation,
stores the resulting JVM AOT cache in the image, and configures cache loading at launch.
No host-generated cache or custom Dockerfile training step is required.
Training must be able to initialize the application without production credentials
or external services. Firebase credentials are resolved lazily on the first send.

The container smoke test sets `-XX:AOTMode=on -Xlog:aot=info` and requires the JVM
to report `Using AOT-linked classes: true`, in addition to passing health and API checks.
This prevents a silently disabled cache from passing CI.
Run `bash .github/scripts/check-container.sh` to verify the actual image.
Image creation and cache loading have not been verified locally because Docker is unavailable.

To disable cache loading temporarily for comparison, launch with
`-e BPL_JVM_AOTCACHE_ENABLED=false`. Rebuild after application or runtime changes;
do not reuse caches across JVM versions, architectures, or different application layouts.
Spring AOT processing and GraalVM native-image compilation are separate mechanisms;
this configuration requests a JVM image, not a native executable.
Measure startup and first-request latency before claiming a performance improvement.

Reference: [Spring Boot OCI image packaging](https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html).
Cache settings: [Paketo Spring Boot buildpack](https://github.com/paketo-buildpacks/spring-boot).
