FROM openjdk:11-jdk
VOLUME /tmp
#ARG JAR_FILE
COPY target/*spring-boot.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
