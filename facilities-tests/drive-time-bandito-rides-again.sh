#!/usr/bin/env bash
[ $# -ne 2 ] && echo "drive-time-bandito-rides-again.sh sentinel client-key" && exit 1
mvn -q -Dsentinel="$1" -Dclient-key="$2" test -Ppssg-request-bot -P'!standard'
