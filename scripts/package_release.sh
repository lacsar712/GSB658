#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
RELEASE_DIR="${ROOT_DIR}/release"
PROJECT_NAME="community_system"
TIMESTAMP="$(date +%Y%m%d_%H%M%S)"
ARCHIVE_PATH="${RELEASE_DIR}/${PROJECT_NAME}_release_${TIMESTAMP}.tar.gz"

mkdir -p "${RELEASE_DIR}"

# 仅打包，不执行任何自动化测试流程
echo "Packaging in lightweight mode (no auto tests)."

INCLUDE_ITEMS=(
  "backend"
  "frontend"
  "database"
  "docs"
  "docker-compose.yml"
  "README.md"
  "00_项目交付清单.md"
)

tar -czf "${ARCHIVE_PATH}" \
  --exclude-vcs \
  --exclude='*/node_modules' \
  --exclude='*/dist' \
  --exclude='*/build' \
  --exclude='*/out' \
  --exclude='*/venv' \
  --exclude='*/.venv' \
  --exclude='*/env' \
  --exclude='*/.git' \
  --exclude='*/.git/*' \
  --exclude='*/target' \
  --exclude='*/target/*' \
  --exclude='*/targrt' \
  --exclude='*/targrt/*' \
  --exclude='*.py' \
  --exclude='*.pyc' \
  --exclude='*/__pycache__' \
  --exclude='*/src/test' \
  --exclude='*/src/test/*' \
  --exclude='*/tests' \
  --exclude='*/tests/*' \
  --exclude='*/test' \
  --exclude='*/test/*' \
  --exclude='*/__tests__' \
  --exclude='*/__tests__/*' \
  --exclude='*.spec.*' \
  --exclude='*.test.*' \
  --exclude='pytest.ini' \
  --exclude='tox.ini' \
  --exclude='jest.config.*' \
  --exclude='vitest.config.*' \
  --exclude='playwright.config.*' \
  --exclude='cypress.config.*' \
  --exclude='*/coverage' \
  --exclude='*/coverage/*' \
  --exclude='*/.pytest_cache' \
  --exclude='*/.pytest_cache/*' \
  --exclude='*/.mypy_cache' \
  --exclude='*/.mypy_cache/*' \
  --exclude='*/cypress' \
  --exclude='*/cypress/*' \
  --exclude='*/playwright' \
  --exclude='*/playwright/*' \
  --exclude='*/.github' \
  --exclude='*/.github/*' \
  --exclude='*/.idea' \
  --exclude='*/.vscode' \
  --exclude='*.tar.gz' \
  --exclude='*.log' \
  --exclude='.DS_Store' \
  -C "${ROOT_DIR}" \
  "${INCLUDE_ITEMS[@]}"

echo "Package created: ${ARCHIVE_PATH}"
