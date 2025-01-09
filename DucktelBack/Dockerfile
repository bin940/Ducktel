FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY settings.gradle .
COPY build.gradle .
COPY ./src ./src
RUN gradle bootJar --no-daemon -x test

FROM openjdk:17
WORKDIR /app
COPY --from=build /app/build/libs/DucktelBack-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
