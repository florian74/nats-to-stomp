FROM maven:3.8.5-openjdk-18-slim as builder
WORKDIR /app
COPY . .
RUN mvn install -DskipTests --quiet

FROM openjdk:18
COPY --from=builder /app/target/nats-to-stomp-1.0-SNAPSHOT-exec.jar /app.jar
RUN mkdir "config"
COPY --from=builder /app/src/main/resources/configuration.yml /config/configuration-override.yml
EXPOSE 8667
ENTRYPOINT ["java", "-jar", "/app.jar", "--server.config.path=file:/config/configuration-override.yml"]