# Build stage
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copy the mvnw and .mvn directory
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Make mvnw executable and download dependencies (for caching)
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copy the source code and build the application
COPY src src
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9091
ENTRYPOINT ["java", "-jar", "app.jar"]
