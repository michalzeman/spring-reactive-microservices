# spring-reactive-microservices

This demo is based on Spring Webflux. There are three services:

- Statistic MS
- Shortener MS
- User MS

## Shortener MS
- responsible by creating of short url
- when user is loading some url based on some key representing shortened url, MS is generaring event ShortenerViewed and publishing it into the Kafka topic "shortener-changed"
- this MS is designed in DDD style and representing Shortener Bounded Context.
- Changes of Shortener aggregate like it was created, updated ... are captured by ShortenerChangedEvent and published into the Kafka topic "shortener-changed"
- Result of changes done on aggregate is also publist as a document event into the Kafka topic "shortener-document". This document event representing current state of Shortener arggregate and it could be used for others services for building of local views in order to avoid direct communication between service. This 
