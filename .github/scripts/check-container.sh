#!/usr/bin/env bash
set -euo pipefail

image="${1:-testserver:smoke}"
docker build -t "$image" .
container="$(docker run -d -p 127.0.0.1::8080 -p 127.0.0.1::8081 "$image")"
cleanup() {
    docker logs "$container" || true
    docker rm -f "$container" >/dev/null
}
trap cleanup EXIT
app_port="$(docker port "$container" 8080/tcp | cut -d: -f2)"
management_port="$(docker port "$container" 8081/tcp | cut -d: -f2)"
ready=false
for attempt in {1..60}; do
    if curl --fail --silent "http://127.0.0.1:$management_port/actuator/health" > /dev/null; then
        ready=true
        break
    fi
    sleep 1
done
if [[ "$ready" != true ]]; then
    echo 'Container did not become healthy within 60 seconds' >&2
    exit 1
fi
[[ "$(curl --fail --silent "http://127.0.0.1:$app_port/hello/smoke")" == 'Hello smoke!' ]]
[[ "$(curl --fail --silent -H 'Accept: application/json' "http://127.0.0.1:$app_port/permission-valid/smoke")" == 'This hello' ]]
status="$(curl --silent --output /dev/null --write-out '%{http_code}' "http://127.0.0.1:$app_port/permission/smoke")"
[[ "$status" == 403 ]]
echo 'Container health, greeting, permission allow and deny checks passed'
