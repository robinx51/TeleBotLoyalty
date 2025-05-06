FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

COPY target/OneC-0.0.1-SNAPSHOT-exec.jar app.jar

EXPOSE 9094

CMD ["java", "-jar", "app.jar"]