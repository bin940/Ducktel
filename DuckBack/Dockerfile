FROM gradle:8.12-jdk17 AS builder
WORKDIR /app
COPY . .
ENV GRADLE_USER_HOME=/home/gradle_cache
RUN mkdir -p /home/gradle_cache && \
    chown -R gradle:gradle /home/gradle_cache && \
    gradle build --no-daemon --stacktrace

FROM openjdk:17
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]