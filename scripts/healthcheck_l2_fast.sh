#!/bin/bash
# L2 fast — entry check. Does NOT boot the backend.
# If backend already running on :8081, hits /actuator/health.
# If not running, returns YELLOW (not RED) with note to boot before work.
# Target: <3s.

set -euo pipefail

PORT=8081
HEALTH_URL="http://localhost:${PORT}/actuator/health"

echo "==> L2 FAST runtime check"

# Check if port is open by attempting a quick connection.
if ! curl -fsS --max-time 2 "$HEALTH_URL" > /tmp/portfolio_health.json 2>/dev/null; then
  echo "  YELLOW: backend not running on :${PORT}"
  echo "  Boot it with: cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev'"
  echo "==> L2 FAST: YELLOW (session may proceed; commit gate will run L2 FULL)"
  exit 0
fi

# Server is up — verify health status is UP.
if grep -q '"status":"UP"' /tmp/portfolio_health.json; then
  echo "  - backend responding, status=UP"
  echo "==> L2 FAST: GREEN"
  exit 0
fi

echo "  RED: /actuator/health returned non-UP status:"
cat /tmp/portfolio_health.json
echo "==> L2 FAST: RED"
exit 1
