#!/usr/bin/env bash

cd $(readlink -f $(dirname $0))

# Destroy old container
docker stop facilities-cdw-db
docker rm facilities-cdw-db

# SQL Server Docker Image (You don't have to install anything!!!)
docker pull mcr.microsoft.com/mssql/server:2017-latest

docker run \
  --name "facilities-cdw-db" \
  -e 'ACCEPT_EULA=Y' \
  -e "SA_PASSWORD=<YourStrong!Passw0rd>" \
  -p 1533:1433 \
  -d mcr.microsoft.com/mssql/server:2017-latest

mvn clean install -Ppopulaterator
