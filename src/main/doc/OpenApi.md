# Static OpenAPI generation

Generate specifications directly from application controllers, request/response
models, and validation annotations:

```sh
./gradlew generateOpenApi
```

On Windows, use `.\gradlew.bat generateOpenApi`.
Generated files:

* `build/openapi/openapi.json`
* `build/openapi/openapi.yaml`

`./gradlew build` also generates and validates these files through `check`.
Generation uses a Spring test context and MockMvc without opening a server port
or requiring cloud credentials. Springdoc is a test-only dependency and Swagger UI
is not included. Packaged applications contain neither Springdoc endpoints nor UI.
The generated files remain build artifacts and are not served by the application.

The generation task validates the expected API paths and request schema before
writing files. There is no manually maintained endpoint list in the specification.
Protobuf wire definitions remain in `src/main/proto/message.proto`.
Custom AOP permission rules are not inferred as authentication schemes.

Reference: [Springdoc OpenAPI](https://springdoc.org/).
