FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="ESI Rabat — GameVerseAcademy"
LABEL org.opencontainers.image.source="https://github.com/AyMB1303/GameVerseAcademy"

WORKDIR /app

# Copy the fat JAR produced by maven-shade-plugin
COPY target/GameVerseAcademy-*.jar app.jar

# Embedded Tomcat listens on 8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
