FROM openjdk:11-jdk
VOLUME /tmp

ENV BROKERS=localhost
ENV ZK_NODES=localhost
ENV MONGO_URI=mongodb://localhost:27017/shortener-ms-db
ENV AUTO_CREATE_TOPICS=false

COPY shortener-impl/target/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar",""]
