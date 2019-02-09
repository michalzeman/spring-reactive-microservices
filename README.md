# spring-reactive-microservices

This demo is based on:
- Spring boot
- Spring Webflux 
- Apache Kafka

There are three services:

- Statistic MS
- Shortener MS
- User MS

## Shortener MS
- responsible by creating of short url
- when user is loading some url based on some key representing shortened url, MS is generaring event ShortenerViewed and publishing it into the Kafka topic "shortener-viewed"
- this MS is designed or trying to be designed in DDD style and representing Shortener Bounded Context.
- Changes of Shortener aggregate like it was created, updated ... are captured by ShortenerChangedEvent and published into the Kafka topic "shortener-changed"
- Result of changes done on aggregate is also publist as a document event into the Kafka topic "shortener-document". This document event representing current state of Shortener arggregate and it could be used for others services for building of local views in order to avoid direct communication between service. This document event is also posiible to use for creating of views e.g. comused by ElaticSearch. Maybe it would be implemented later like CQRS.

## Statistic MS
- this MS is downstream of Shortener MS
- responsible by calculation of some static related with Shortener MS
- consumer of "shortener-viewed" Kafka topic
- providing of API for VIEWS like how many times was display particular URL

## User MS
- TBD not ready
