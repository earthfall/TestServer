# Spring Modulith

Spring Modulith 2.1.1 models the direct subpackages of `org.example` as
application modules. Module annotations live in `package-info.java` and
declare an explicit allowlist of dependencies.

| Module | Allowed dependencies |
| --- | --- |
| `annotation` | None |
| `aop` | `security`, `annotation` |
| `config` | None |
| `event` | None |
| `exception` | None |
| `push` | `annotation`, `config` |
| `security` | `event`, `exception`, `annotation` |
| `transform` | `annotation` |
| `util` | None |

Each module's root package is its public API. Nested packages are internal
unless explicitly exported. Common annotations are the public API of the
independent `annotation` module. Configuration properties are exposed by `config`.

`SecurityAspect` remains in `aop`. `FirebaseClientConfiguration` belongs to
`push`, while `AppProperties` belongs to `config`. The dependency direction
is `push` to `config`, with no reverse dependency. Common annotations belong
to `annotation`; there is no `shared` module. HTTP client settings remain in YAML.

`ModularityTest` checks for cycles, access to internal packages, and
dependencies not included in the allowlists. `TransformModuleTest` uses
`@ApplicationModuleTest` to bootstrap and test the transformation module
in isolation. Both tests run as part of the regular Gradle build.

```sh
./gradlew test
```

Spring application events retain their synchronous behavior. This setup
does not introduce a persistent event publication registry or a database.
