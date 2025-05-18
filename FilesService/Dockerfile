FROM --platform=linux/arm64 gradle:8.7-jdk21 AS build
WORKDIR /app

COPY build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle . .
RUN apt-get update && apt-get install -y netcat
RUN chmod +x ./gradlew
RUN ./gradlew generateProto
RUN ./gradlew build -x test --no-daemon

FROM --platform=linux/arm64 openjdk:21-jdk-slim
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 9090
CMD ["java", "-jar", "app.jar"]
