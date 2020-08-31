FROM openjdk:11-jdk
VOLUME /tmp

ENV STATISTIC_SERVICE_DOMAIN=localhost
ENV SHORTENER_SERVICE_DOMAIN=localhost
ENV USER_SERVICE_DOMAIN=localhost

COPY api-gateway/target/*SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar",""]