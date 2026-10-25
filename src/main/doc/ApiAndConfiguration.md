# API and configuration contracts

## Send a notification

`POST /send` now requires a JSON request body and returns the FCM message ID.
It no longer sends a hard-coded sample or echoes a request-shaped response.

```json
{
  "to": "device-registration-token",
  "notification": {"title": "Hello", "body": "Message"},
  "data": {"entry": "value"},
  "priority": "high"
}
```

`to` is required. `/topics/updates` addresses a topic instead of a device.
`notification`, `data`, and `priority` are optional. A supplied notification
requires nonblank title and body; priority is `high` or `normal`.

```json
{"name": "projects/my-project/messages/message-id"}
```

The client calls `/v1/projects/{projectId}/messages:send` with a `message`
envelope and an OAuth Bearer token. Tokens are obtained from Google Application
Default Credentials, scoped for Firebase Messaging, and refreshed when needed.
Set `FIREBASE_PROJECT_ID` to the real project ID and configure ADC, for example
through a workload identity or `GOOGLE_APPLICATION_CREDENTIALS` pointing to a
service-account credential file outside the repository. Credentials are resolved
on the first send; startup and AOT training do not contact Google.

The default project `demo-testserver` is a development placeholder, not a
configured Firebase project. `FIREBASE_API_URL` defaults to
`https://fcm.googleapis.com` and can be overridden for a mock server. Connection
and read timeouts are 2 and 5 seconds. Sends are not automatically retried,
because a timed-out send may already have delivered a notification.

## Error responses

Errors use RFC 9457 `application/problem+json` responses:

| Condition | HTTP status |
| --- | --- |
| Invalid JSON, binding, validation, or input | 400 |
| Permission denied or user missing | 403 |
| Unexpected application failure | 500 |
| Upstream authentication, server, transport, or empty-response failure | 502 |
| Upstream socket or HTTP timeout | 504 |

Framework errors retain their standard status codes. Unexpected and upstream
exceptions are logged server-side; their raw messages are not returned.
The sample permission service still uses a fixed permission string and is not
a user authentication or production authorization implementation.

## DynamoDB and property validation

Outside the `local` profile, AWS uses its default credential chain; no static
AWS credentials are stored in the main configuration. `AWS_REGION` defaults to
`us-east-1`, and no endpoint override is applied by default.

For DynamoDB Local:

```sh
./gradlew bootRun --args='--spring.profiles.active=local'
```

The local profile selects `http://localhost:8000` and local-only dummy
credentials. Test properties keep a local endpoint; failure injection exists
only in test code. Required greeting, region, and Firebase project settings are
validated at configuration binding. Firebase API URLs and optional DynamoDB
endpoint overrides must be absolute HTTP(S) URLs.

## Resource bundles and transformations

JSON resource bundles accept only an object of string values, including empty
strings. Null, numeric, array, and object values are rejected. Key enumeration
is fresh on every call, includes parent keys once, and local values override
parent values.

Transformations support mutable POJOs with a no-argument constructor. Static
and synthetic fields are skipped; final instance fields are rejected with a
`TransformException`. Private inherited fields are supported. Cycles and shared
references between `Transformable` objects are preserved within one transform
operation. Nulls and primitives are preserved. Collections and non-transformable
objects remain shallow references; their contents are not recursively copied.

References:

- [FCM HTTP v1](https://firebase.google.com/docs/cloud-messaging/send/v1-api)
- [Spring error responses](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html)
- [HTTP client configuration](https://docs.spring.io/spring-boot/reference/io/rest-client.html)
