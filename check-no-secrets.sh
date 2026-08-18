#!/usr/bin/env bash
# Проверяет, что в репозитории нет наших ключей и учётных данных.
#
#   ./check-no-secrets.sh
#
# Запускать перед каждой передачей шаблона: репозиторий публичный, и всё, что
# в нём лежит, уезжает партнёрам. Скрипт смотрит только отслеживаемые git файлы —
# то, что реально уедет, а не рабочий мусор рядом.
#
# Что ищем и почему:
#
#   Firebase. В шаблоне должны лежать ЗАГЛУШКИ google-services.json, а не наши
#   файлы. Партнёр обязан положить свой; parseConfig это проверяет и падает с
#   понятным сообщением, если клиента под его applicationId в файле нет. До
#   2026-08-18 здесь лежали наши проекты travelapprelease-app и
#   travelapp-debug-app с пакетами com.travelapp (TAAD-1084).
#
#   Ключи подписи. .gitignore закрывает signing.properties, *.keystore и *.jks,
#   но не покрывает файл, который УЖЕ был закоммичен, и не знает про .p12/.bks.
#
#   Ключи в конфигурации. marker и api_key в config/app_config.json заполняет
#   партнёр. Наши значения там оказаться не должны.
set -uo pipefail
cd "$(dirname "$0")"
FAILED=0

fail() { echo "  ✗ $1"; FAILED=1; }
ok()   { echo "  ✓ $1"; }

echo "== Firebase"
# Наш project_number и наши пакеты — самые надёжные признаки: они не меняются
# от файла к файлу и не зависят от того, как называется проект.
HITS=$(git grep -l -E "155726775133|733484587469|travelapprelease-app|travelapp-debug-app" -- '*.json' 2>/dev/null)
if [ -n "$HITS" ]; then
    fail "наш Firebase-проект в файлах:"
    echo "$HITS" | sed 's/^/      /'
else
    ok "наших проектов Firebase нет"
fi

PKGS=$(git grep -l -E '"package_name": *"com\.travelapp' -- '*.json' 2>/dev/null)
if [ -n "$PKGS" ]; then
    fail "наши имена пакетов в конфигурации Firebase:"
    echo "$PKGS" | sed 's/^/      /'
else
    ok "имена пакетов не наши"
fi

echo "== Ключи подписи"
KEYS=$(git ls-files | grep -iE '\.(keystore|jks|p12|bks)$|(^|/)signing\.properties$' || true)
if [ -n "$KEYS" ]; then
    fail "в репозитории есть файлы ключей:"
    echo "$KEYS" | sed 's/^/      /'
else
    ok "файлов ключей нет"
fi

echo "== Конфигурация приложения"
if [ -f config/app_config.json ]; then
    VALUES=$(python3 - <<'PY'
import json
c = json.load(open("config/app_config.json"))
bad = []
marker = c["constants"].get("marker") or ""
if marker not in ("", "0"):
    bad.append(f"constants.marker = {marker!r}")
for key in ("api_key", "appsflyer_dev_key"):
    if c["constants"].get(key):
        bad.append(f"constants.{key} заполнен")
adv = c.get("advertising") or {}
for key in ("appodeal_api_key", "google_admob_app_id"):
    if adv.get(key):
        bad.append(f"advertising.{key} заполнен")
print("\n".join(bad))
PY
)
    if [ -n "$VALUES" ]; then
        fail "в config/app_config.json заполнены ключи:"
        echo "$VALUES" | sed 's/^/      /'
    else
        ok "ключи в конфигурации пустые"
    fi
fi

echo
if [ "$FAILED" -eq 0 ]; then
    echo "ИТОГ: чисто, репозиторий можно передавать."
else
    echo "ИТОГ: найдено то, что не должно уезжать партнёрам. Передавать нельзя."
fi
exit "$FAILED"
