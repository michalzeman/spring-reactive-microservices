#!/usr/bin/env bash

docker-compose -f docker-compose.yml -f docker-compose-api-gateway.yml -f docker-compose-shortener-service.yml -f docker-compose-statistic-service.yml -f docker-compose-user-service.yml "$@"