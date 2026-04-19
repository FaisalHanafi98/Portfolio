#!/bin/bash
# L2 FULL — commit gate. Real user path, end to end.
# 1. Boot backend (dev profile, H2) if not already up.
# 2. Wait until /actuator/health returns UP (max 60s).
# 3. GET /api/v1/projects  -> expect 200 + JSON array length >= 1 (proves DataSeeder ran).
# 4. Parse first slug from response, GET /api/v1/projects/<slug> -> expect 200 (proves routing).
# 5. Build frontend -> expect frontend/dist/index.html exists.
# 6. Tear down backend if this script booted it.
#
# Exit 0 = GREEN. Exit 1 = RED.
# Target: 40-90s depending on boot cache.

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PORT=8081
HEALTH_URL="http://localhost:${PORT}/actuator/health"
PROJECTS_URL="http://localhost:${PORT}/api/v1/projects"
BOOT_LOG="/tmp/portfolio_bootrun.log"
BOOT_PID=""
WE_BOOTED=0

echo "==> L2 FULL runtime check"

teardown() {
  if [ "$WE_BOOTED" = "1" ] && [ -n "$BOOT_PID" ]; then
    echo "  - tearing down backend (pid=$BOOT_PID)"
    kill "$BOOT_PID" 2>/dev/null || true
    # Give it 5s to die gracefully, then force.
    for _ in 1 2 3 4 5; do
      kill -0 "$BOOT_PID" 2>/dev/null || break
      sleep 1
    done
    kill -9 "$BOOT_PID" 2>/dev/null || true
  fi
}
trap teardown EXIT

# Step 1: check if already running.
if curl -fsS --max-time 2 "$HEALTH_URL" > /dev/null 2>&1; then
  echo "  - backend already running on :${PORT}"
else
  echo "  - booting backend (dev profile)"
  cd "$PROJECT_ROOT/backend"
  ./gradlew bootRun --args='--spring.profiles.active=dev' > "$BOOT_LOG" 2>&1 &
  BOOT_PID=$!
  WE_BOOTED=1

  # Wait up to 60s for health.
  echo "  - waiting for /actuator/health UP (max 60s)"
  ready=0
  for i in $(seq 1 60); do
    if curl -fsS --max-time 1 "$HEALTH_URL" 2>/dev/null | grep -q '"status":"UP"'; then
      ready=1
      echo "  - backend UP after ${i}s"
      break
    fi
    sleep 1
  done

  if [ "$ready" = "0" ]; then
    echo "  RED: backend failed to reach UP within 60s"
    echo "  ---- last 40 lines of bootRun log ----"
    tail -40 "$BOOT_LOG" || true
    exit 1
  fi
fi

# Step 2: GET /api/v1/projects — must return 200 with non-empty array.
# Retry up to 20s: DataSeeder may still be committing when backend first reports UP.
echo "  - GET $PROJECTS_URL (with up to 20s retry for DataSeeder)"
projects_ready=0
for i in $(seq 1 20); do
  if curl -fsS --max-time 5 "$PROJECTS_URL" > /tmp/portfolio_projects.json 2>/dev/null; then
    if grep -q '"slug"' /tmp/portfolio_projects.json; then
      projects_ready=1
      echo "  - projects list returned with slugs (attempt ${i})"
      break
    fi
  fi
  sleep 1
done

if [ "$projects_ready" = "0" ]; then
  echo "  RED: /api/v1/projects returned no slugs after 20s (DataSeeder may not have run)"
  cat /tmp/portfolio_projects.json 2>/dev/null || echo "(no response)"
  exit 1
fi

# Step 3: parse first slug, GET by slug.
# Use python for robust JSON parsing rather than regex surgery.
FIRST_SLUG=$(python -c "import json,sys;d=json.load(sys.stdin);items=d.get('data',d) if isinstance(d,dict) else d;print(items[0]['slug'] if isinstance(items,list) and items else '')" < /tmp/portfolio_projects.json 2>/dev/null || echo "")

if [ -z "$FIRST_SLUG" ]; then
  echo "  RED: could not parse first slug from /api/v1/projects response"
  exit 1
fi

SLUG_URL="http://localhost:${PORT}/api/v1/projects/${FIRST_SLUG}"
echo "  - GET $SLUG_URL"
if ! curl -fsS --max-time 10 "$SLUG_URL" > /dev/null; then
  echo "  RED: GET by slug failed"
  exit 1
fi
echo "  - single-project lookup OK"

# Step 4: frontend build.
echo "  - building frontend"
cd "$PROJECT_ROOT/frontend"
if [ ! -d "node_modules" ]; then
  echo "  - node_modules missing, running npm ci"
  npm ci --silent
fi
if ! npm run build --silent; then
  echo "  RED: frontend build failed"
  exit 1
fi

if [ ! -f "$PROJECT_ROOT/frontend/dist/index.html" ]; then
  echo "  RED: frontend/dist/index.html missing after build"
  exit 1
fi
echo "  - frontend build produced dist/index.html"

echo "==> L2 FULL: GREEN"
exit 0
