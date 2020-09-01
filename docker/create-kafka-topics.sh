#!/bin/bash

KAFKA_BROKER=localhost

kafka-topics --create \
--bootstrap-server "$KAFKA_BROKER":9092 \
--replication-factor 1 \
--partitions 1 \
--topic shortener-changed \
&&
kafka-topics --create \
--bootstrap-server "$KAFKA_BROKER":9092 \
--replication-factor 1 \
--partitions 1 \
--topic shortener-document \
&&
kafka-topics --create \
--bootstrap-server "$KAFKA_BROKER":9092 \
--replication-factor 1 \
--partitions 1 \
--topic shortener-viewed \
&&
kafka-topics --create \
--bootstrap-server "$KAFKA_BROKER":9092 \
--replication-factor 1 \
--partitions 1 \
--topic user-document \
&&
kafka-topics --create \
--bootstrap-server "$KAFKA_BROKER":9092 \
--replication-factor 1 \
--partitions 1 \
--topic user-changed