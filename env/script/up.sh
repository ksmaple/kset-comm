#!/usr/bin/env bash
# 启动本地公共环境。
set -euo pipefail

BUILD=false
NO_CAT=false

for arg in "$@"; do
  case "${arg}" in
    --build) BUILD=true ;;
    --no-cat) NO_CAT=true ;;
    *)
      echo "未知参数: ${arg}"
      echo "用法: env/script/up.sh [--build] [--no-cat]"
      exit 1
      ;;
  esac
done

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

bash "${SCRIPT_DIR}/sync.sh"

if [[ ! -f "${ENV_DIR}/.env" ]]; then
  echo "缺少 env/.env，请先执行: cp env/.env.example env/.env" >&2
  exit 1
fi

cd "${ENV_DIR}"

COMPOSE=(docker compose --env-file .env -f docker-compose.yml)
if [[ "${NO_CAT}" != "true" ]]; then
  COMPOSE+=(-f cat/docker-compose.yml)
fi

UP=("${COMPOSE[@]}" up -d)
if [[ "${BUILD}" == "true" ]]; then
  UP+=(--build)
fi

echo "docker ${UP[*]}"
"${UP[@]}"
"${COMPOSE[@]}" ps
