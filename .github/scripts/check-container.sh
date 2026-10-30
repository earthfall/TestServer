#!/usr/bin/env bash
set -euo pipefail

image="${1:-testserver:smoke}"
mkdir -p build/ci
results=build/ci/container-results.txt
: > "$results"
record() { printf '%s=%s\n' "$1" "$2" >> "$results"; }
stage='Buildpacks image'
container=''
cleanup() {
    exit_code=$?
    trap - EXIT
    if (( exit_code != 0 )); then record "$stage" 'Failed'; fi
    if [[ -n "$container" ]]; then
        docker logs "$container" || true
        docker rm -f "$container" >/dev/null || true
    fi
    exit "$exit_code"
}
trap cleanup EXIT
record 'Image' "$image"
record 'Requested JVM' 'Java 25'
record 'JVM AOT cache configuration' 'Enabled'
build_started=$SECONDS
./gradlew bootBuildImage --imageName="$image" --console=plain
record "$stage" 'Passed'
record 'Image build duration' "$((SECONDS - build_started)) s"
stage='Image inspection'
record 'Image ID' "$(docker image inspect --format '{{.Id}}' "$image")"
record 'Image size (bytes)' "$(docker image inspect --format '{{.Size}}' "$image")"
record 'Platform' "$(docker image inspect --format '{{.Os}}/{{.Architecture}}' "$image")"
stage='Container startup'
startup_started=$SECONDS
container="$(docker run -d -e JAVA_TOOL_OPTIONS='-XX:AOTMode=on -Xlog:aot=info' -p 127.0.0.1::8080 -p 127.0.0.1::8081 "$image")"
stage='Health endpoint'
app_port="$(docker port "$container" 8080/tcp | cut -d: -f2)"
management_port="$(docker port "$container" 8081/tcp | cut -d: -f2)"
ready=false
for attempt in {1..60}; do
    if curl --fail --silent --max-time 2 "http://127.0.0.1:$management_port/actuator/health" > /dev/null; then
        ready=true
        break
    fi
    sleep 1
done
if [[ "$ready" != true ]]; then
    echo 'Container did not become healthy within 60 seconds' >&2
    exit 1
fi
record "$stage" 'Passed'
record 'Startup to health' "$((SECONDS - startup_started)) s"
stage='Greeting API'
[[ "$(curl --fail --silent "http://127.0.0.1:$app_port/hello/smoke")" == 'Hello smoke!' ]]
record "$stage" 'Passed'
stage='Permission allow'
[[ "$(curl --fail --silent -H 'Accept: application/json' "http://127.0.0.1:$app_port/permission-valid/smoke")" == 'This hello' ]]
record "$stage" 'Passed'
stage='Permission deny'
status="$(curl --silent --output /dev/null --write-out '%{http_code}' "http://127.0.0.1:$app_port/permission/smoke")"
[[ "$status" == 403 ]]
record "$stage" 'Passed (HTTP 403)'
stage='JVM AOT cache loading'
if ! docker logs "$container" 2>&1 | grep -F 'Using AOT-linked classes: true' > /dev/null; then
    echo 'JVM did not report using AOT-linked classes' >&2
    exit 1
fi
record "$stage" 'Passed: AOT-linked classes loaded'
echo 'Container health, API contracts, and JVM AOT cache loading checks passed'
