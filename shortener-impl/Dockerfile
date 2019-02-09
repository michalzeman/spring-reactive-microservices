FROM openjdk:8-jdk-alpine
VOLUME /tmp
#ARG JAR_FILE
COPY target/*spring-boot.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]