#!/usr/bin/env bash
set -euo pipefail

puerto=8080
url="http://127.0.0.1:$puerto"
if curl --fail --silent --max-time 2 "$url/api/estado" > /dev/null; then
    exit 0
fi

bash compilar.sh
nohup env HOST=0.0.0.0 java -cp out Main --web "$puerto" </dev/null >out/web.log 2>&1 &

if ! curl --fail --silent --show-error --retry 15 --retry-delay 1 \
    --retry-connrefused --max-time 2 "$url/api/estado" > /dev/null; then
    cat out/web.log >&2
    exit 1
fi

echo "SmartETL disponible en $url"
