FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="ESI Rabat — GameVerseAcademy"
LABEL org.opencontainers.image.source="https://github.com/AyMB1303/GameVerseAcademy"

WORKDIR /app

# Copy the fat JAR produced by maven-shade-plugin
COPY target/GameVerseAcademy-*.jar app.jar

# ServerConfig.java resolves webapp resources relative to working dir:
# new File("src/main/webapp") → /app/src/main/webapp
COPY src/main/webapp src/main/webapp

# App listens on 6060
EXPOSE 6060

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
