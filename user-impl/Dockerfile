FROM openjdk:11-jdk
VOLUME /tmp

ENV BROKERS=localhost:9092
ENV ZK_NODES=localhost
ENV MONGO_DB_HOST=localhost
ENV MONGO_URI=mongodb://localhost:27017/user-ms-db
ENV AUTO_CREATE_TOPICS=false

COPY user-impl/target/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar",""]
