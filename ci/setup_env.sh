#!/bin/bash

# Собираем все переменные в переменную envs
envs="
MAPKIT_API_KEY=${MAPKIT_API_KEY}
IIKO_API_KEY=${IIKO_API_KEY}
"

# Фильтруем и сохраняем в apikeys.properties
echo "$envs" | grep -E '.+=.+' > apikeys.properties