#!/bin/bash
# Portfolio healthcheck wrapper.
# Dispatches to l1 (structural), l2-fast (entry check), or l2-full (commit gate).
#
# Usage:
#   scripts/healthcheck.sh --l1
#   scripts/healthcheck.sh --l2-fast
#   scripts/healthcheck.sh --l2-full
#   scripts/healthcheck.sh --all        # runs l1 then l2-full

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODE="${1:-}"

case "$MODE" in
  --l1)
    exec "$SCRIPT_DIR/healthcheck_l1.sh"
    ;;
  --l2-fast)
    exec "$SCRIPT_DIR/healthcheck_l2_fast.sh"
    ;;
  --l2-full)
    exec "$SCRIPT_DIR/healthcheck_l2_full.sh"
    ;;
  --all)
    "$SCRIPT_DIR/healthcheck_l1.sh"
    "$SCRIPT_DIR/healthcheck_l2_full.sh"
    ;;
  *)
    echo "Usage: $0 {--l1|--l2-fast|--l2-full|--all}"
    exit 2
    ;;
esac
