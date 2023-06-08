FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY aliensupremacyserver-0.1-Alpha.jar app.jar
ENTRYPOINT ["java","-jar", "/app.jar"]
