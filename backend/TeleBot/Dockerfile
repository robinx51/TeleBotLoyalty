FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/TeleBot-1.0.1-SNAPSHOT-exec.jar app.jar

EXPOSE 9093

CMD ["java", "-jar", "app.jar"]