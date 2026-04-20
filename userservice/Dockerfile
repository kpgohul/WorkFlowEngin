# ====== Build Stage ======
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
# Download dependencies first
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ====== Run Stage ======
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 9097
ENTRYPOINT ["java", "-jar", "app.jar"]
