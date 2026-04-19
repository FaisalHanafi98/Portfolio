#!/bin/bash
# L1 structural check — no runtime. Compile backend, typecheck frontend.
# Target: <30s on warm cache, <90s cold.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

echo "==> L1 structural check"

# 1. Backend compile (no tests, just compilation).
cd "$PROJECT_ROOT/backend"
if [ -f "./gradlew" ]; then
  echo "  - compiling backend (./gradlew compileJava)"
  ./gradlew compileJava --quiet
else
  echo "  ! gradlew not found in backend/" >&2
  exit 1
fi

# 2. Frontend typecheck (skip if node_modules missing — flag, don't fail L1).
cd "$PROJECT_ROOT/frontend"
if [ ! -d "node_modules" ]; then
  echo "  ! frontend/node_modules missing — run 'npm ci' before L2"
else
  echo "  - typechecking frontend (tsc --noEmit)"
  npx tsc --noEmit
fi

echo "==> L1: PASS"
