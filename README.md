Copyright 2023 Michal Zeman, zeman.michal@yahoo.com

Licensed under the Creative Commons Attribution (CC BY) license. You are free to share, copy, distribute, 
and adapt this work, provided you give appropriate credit to the original author Michal Zeman, zeman.michal@yahoo.com.

To view a copy of the license, visit https://creativecommons.org/licenses/by/4.0/

# spring-reactive-microservices

This demo is based on:
- Spring boot
- Spring Webflux 
- Apache Kafka
- Akka persistence for event sourcing support

There are services:

- Api-gateway
- Statistic MS
- Shortener MS
- User MS

## Shortener MS
- responsible by creating of short url
- when user is loading some url based on some key representing shortened url, MS is generating event ShortenerViewed and publishing it into the Kafka topic "shortener-viewed"
- this MS is designed or trying to be designed in DDD style and is representing Shortener Bounded Context.
- Changes of Shortener aggregate like it was created, updated ... are captured by ShortenerChangedEvent and published into the Kafka topic "shortener-changed"
- Result of changes done on aggregate is also published as a document event into the Kafka topic "shortener-document". 
This document event representing current state of Shortener aggregate, and it could be used for others services for 
building of local views in order to avoid direct communication between service. This document event is also possible to use for creating of views e.g. consumed by ElasticSearch. Maybe it would be implemented later like CQRS.

## Statistic MS
- this MS is downstream of Shortener MS
- responsible by calculation of some static related with Shortener MS
- consumer of "shortener-viewed" Kafka topic
- providing of API for VIEWS like how many times was displayed particular URL

## User MS
- TBD not ready

# How to run microservices locally
## Requirements
- installed Docker
- Java SDK 11
- Insomnia for the REST API collection
- CURL
## Build and run locally
### Run it as Dockerized services
1) From to root directory, execute the build with skipped tests
   ```
   ./mvnw clean install -DskipTests
   ```
   - this will start all services as docker containers
2) From to `./docker` dir. execute
   ```
   docker-compose -f docker-compose.yml -f docker-compose-api-gateway.yml -f docker-compose-shortener-service.yml -f docker-compose-statistic-service.yml -f docker-compose-user-service.yml up -d
   ```
3) For the verification of running services, use CURL commands
   - Create user:
   ```
   curl --request POST \
   --url http://localhost:8080/users \
   --header 'Content-Type: application/json' \
   --data '{
   "firstName": "FirstNameTest",
   "lastName": "LastNameTest"
   }'
   ```
   - this will verify the running User MS
   - List all users
   ```
   curl --request GET \
   --url http://localhost:8080/users/
   ```
   - Add contact information to the created user
   ```
   curl --request PUT \
   --url http://localhost:8080/users/[user_id]/contactinformation \
   --header 'Content-Type: application/json' \
   --data '{
   "email": "test@email.com",
   "phoneNumber": "+421999009001"
   }'
   ```
   - Get details of the created user
   ```
   curl --request GET \
   --url http://localhost:8080/users/[user_id]
   ```
   - List all events published by User MS into the Kafka topic and consumed by Statistic MS
   ```
   curl --request GET \
   --url http://localhost:8080/statistics
   ```
   - Create a shortener, this will test Shortener MS
   ```
   curl --request POST \
   --url http://localhost:8080/shorteners/ \
   --header 'Content-Type: application/json' \
   --data '{
   "url":"https://www.reactivemanifesto.org",
   "userId": "a6ed70a3-8f55-4513-acd0-96768a0e0e0c"
   }
   '
   ```
   - where "userId": "a6ed70a3-8f55-4513-acd0-96768a0e0e0c" is user id of some created user, it will be different from case to case
   - Test created shortener link
      - into the browser, enter the link `http://localhost:8080/shorteners/map/${shortener-key}`
      - placeholder `${shortener-key}` is an attribute you have received as a response body from the creation e.g. `"key": "2be71aba-4ff5-45c5-a3bb-7287a69e7e9d",`

You can check all events related to any action or changes done in the microservices ` user MS` and `Shortener MS` via `Statistic MS` with the following command:
```
curl --request GET \
--url http://localhost:8080/statistics
```

### Run necessary Docker infrastructure for the local development
- from the `docker` directory, execute the following command to run Apache Kafka and other services:
```
docker compose -f docker-compose.yml up -d
```
- Then, you can build the project with unit tests:
```
./mvnw clean install
```