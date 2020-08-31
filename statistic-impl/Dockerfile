FROM openjdk:11-jdk
VOLUME /tmp

ENV BROKERS=localhost
ENV ZK_NODES=localhost
ENV MONGO_DB_HOST=localhost
ENV AUTO_CREATE_TOPICS=false

COPY statistic-impl/target/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar",""]