# Getting started and verification

TestServer is an experimental server for validating Spring Boot deployment and API technologies.
It uses Java 25, the Gradle Wrapper, MVC virtual threads, Protobuf, and Firebase FCM v1.
It is not a production service with user authentication.

## Run locally

Install JDK 25 and configure `JAVA_HOME`. Run these commands from the repository root:

```powershell
.\gradlew.bat bootRun
curl.exe http://localhost:8080/hello/world
curl.exe http://localhost:8081/actuator/health
curl.exe -H "Accept: application/json" http://localhost:8080/permission-valid/world
curl.exe -i http://localhost:8080/permission/world
```

The default responses are `Hello world!`, health status, `This hello`, and HTTP 403,
respectively. On Linux, replace `.\gradlew.bat` with `./gradlew`.
Startup and these requests do not require Firebase or DynamoDB servers.
For DynamoDB Local, start with `--args='--spring.profiles.active=local'`.
Prepare a separate DynamoDB Local server before invoking actual database operations.

## Run tests

Static API specifications are generated from code during the build. See the
[OpenAPI guide](OpenApi.md) for JSON, YAML, and generation commands.

```powershell
.\gradlew.bat build
```

The test report is generated at `build/reports/tests/test/index.html`.
Tests cover module boundaries, input validation, upstream failures, permission checks,
Protobuf responses, transformations, and virtual threads.
Firebase tests use mock responses and substitute credentials; they do not verify
actual notification delivery or cloud permissions.

In a Bash environment, run these additional checks:

```sh
bash .github/scripts/check-nullness.sh
bash .github/scripts/check-container.sh
```

The second command requires JDK 25, Docker, and curl. It uses Spring Boot Buildpacks. It builds the image, checks health,
the greeting endpoint, and permission allow/deny responses in a temporary container,
then removes the container. Host ports are assigned dynamically and bound only to
loopback. GitHub Actions runs the same checks after building and testing the project.

## Send real Firebase notifications

Configure `FIREBASE_PROJECT_ID` and Google Application Default Credentials.
If using a service-account file, keep it outside the repository and set
`GOOGLE_APPLICATION_CREDENTIALS` to its path. Credentials are read on the first send.
See [API and configuration contracts](ApiAndConfiguration.md) for request bodies
and configuration details.

`POST /send` sends real notifications and currently has no caller authentication
or rate limiting. Run this server in a trusted local test environment.
Before making it available in a shared environment, implement caller authentication,
send permissions, and rate limits. Overriding `FIREBASE_API_URL` alone does not mock
Google authentication.

## Implementation scope and further verification

* The permission example compares a fixed string and has no per-user permission store.
* Protobuf is a serialization format. Transport encryption depends on deployment TLS settings.
* Spring events run synchronously on the publishing thread.
* Container images are built with Spring Boot Buildpacks using `bootBuildImage`.
  See [container builds and AOT scope](AotCache.md) for commands and limitations.
* Enabling virtual threads does not establish a measured performance improvement.
  Compare throughput, p95 latency, and error rates with virtual threads enabled and disabled,
  using identical mock upstream delays, concurrency, and JVM resources.
  Do not claim performance targets have been met before collecting measurements.

Further reading: [AOT caching](AotCache.md), [nullness checks](Nullness.md),
and [module structure](Modulith.md).
