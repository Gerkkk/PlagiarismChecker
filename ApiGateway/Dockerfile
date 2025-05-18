FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle . .
RUN apt-get update && apt-get install -y netcat
RUN ./gradlew generateProto
RUN ./gradlew build -x test --no-daemon
RUN gradle dependencies --refresh-dependencies && \
    gradle build -x test --no-daemon

#RUN apt-get update && \
#    apt-get install -y curl unzip ca-certificates && \
#    curl --http1.1 -L https://github.com/fullstorydev/grpcurl/releases/download/v1.8.6/grpcurl_1.8.6_linux_arm64.tar.gz -o grpcurl.tar.gz && \
#    tar -xvf grpcurl.tar.gz && \
#    mv grpcurl /usr/local/bin/ && \
#    rm grpcurl.tar.gz && \
#    curl --http1.1 -L https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/v0.4.11/grpc-health-probe-linux-arm64 -o /usr/local/bin/grpc-health-probe && \
#    chmod +x /usr/local/bin/grpc-health-probe && \
#    apt-get clean && rm -rf /var/lib/apt/lists/*

# Run stage
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080 9090
CMD ["java", "-jar", "app.jar"]