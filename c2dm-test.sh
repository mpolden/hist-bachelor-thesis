#!/usr/bin/env bash

DEVICE_ID="$1"
TEXT="$2"

URL="http://127.0.0.1:9000/c2dm/put"
EPOCH=$(date "+%s000")
DATE=$(echo "$EPOCH" | date "+%F %T")
CURL="/usr/bin/curl"

E_MISSINGARGS=1
E_CURLNOTFOUND=2

if [[ -z "$DEVICE_ID" || -z "$TEXT" ]]; then
    echo "usage: $0 <device_id> <text>"
    exit $E_MISSINGARGS
fi

if [[ ! -x "$CURL" ]]; then
    echo "$CURL was not found or is not executable"
    exit $E_CURLNOTFOUND
fi

JSON="[{\"date\": \"$DATE\",\"amount\": 133.7,\"text\": \"$TEXT\",\"internal\": false,\"timestamp\": $EPOCH,\"dirty\": true,\"user\": {\"deviceId\": \"$DEVICE_ID\",\"id\": 1}}]"

$CURL \
    --data-urlencode "registrationId=$DEVICE_ID" \
    --data-urlencode "json=$JSON" $URL

exit $?
