FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="ESI Rabat — GameVerseAcademy"
LABEL org.opencontainers.image.source="https://github.com/AyMB1303/GameVerseAcademy"

WORKDIR /app

# Copy the fat JAR produced by maven-shade-plugin
COPY target/GameVerseAcademy-*.jar app.jar

# App listens on 6060
EXPOSE 6060

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
