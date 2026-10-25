#!/usr/bin/env bash
set -euo pipefail

./gradlew compileValidNullnessFixture --console=plain

assert_rejected() {
    local task="$1"
    shift
    local log
    log="$(mktemp)"
    if ./gradlew "$task" --console=plain >"$log" 2>&1; then
        cat "$log"
        echo "Expected $task to fail compilation" >&2
        exit 1
    fi
    for diagnostic in "$@"; do
        if ! grep -F "$diagnostic" "$log"; then
            cat "$log"
            echo "Missing expected compiler diagnostic: $diagnostic" >&2
            exit 1
        fi
    done
}

assert_rejected compileInvalidNullnessFixture \
    '[NullAway] returning @Nullable expression' \
    '[NullAway] dereferenced expression' \
    '[NullAway] incompatible types'
assert_rejected compileUnmarkedNullnessFixture '[RequireExplicitNullMarking]'
