# JSpecify nullness checks

Gradle runs NullAway in JSpecify mode through Error Prone when compiling main
and test Java sources. Nullness violations and unrecognized JSpecify annotation
locations are compiler errors, so the existing CI `./gradlew build` step fails
on violations. Other Error Prone checks are disabled to keep this check focused.

Run locally with Java 25:

```sh
./gradlew compileJava compileTestJava
./gradlew build
```

Each handwritten Java package declares `@NullMarked` in `package-info.java`.
Use `@Nullable` for values that can be absent and check them before dereferencing.
JUnit lifecycle methods and Spring-injected fields use NullAway's built-in
initialization support. Test helper fields initialized indirectly must declare
their actual nullable state until initialization has been checked.

Files under `build/`, including Protobuf and Spring AOT sources, are excluded
from analysis. Lombok-generated members use NullAway's built-in support for
`lombok.Generated`; handwritten methods in Lombok classes remain checked.

These checks do not prove that reflection, deserialization, or an omitted Lombok
builder field satisfies a non-null contract. Validate required values at those
boundaries when runtime enforcement is needed. JSpecify mode is still evolving
and does not guarantee full conformance to every part of the specification.

References:

- [NullAway JSpecify support](https://github.com/uber/NullAway/wiki/JSpecify-Support)
- [NullAway configuration](https://github.com/uber/NullAway/wiki/Configuration)
- [Lombok compatibility](https://github.com/uber/NullAway#lombok)
