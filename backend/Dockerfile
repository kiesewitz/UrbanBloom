# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy the parent pom and all module poms first to cache dependencies
COPY pom.xml .
COPY shared/pom.xml shared/
COPY module-catalog/pom.xml module-catalog/
COPY module-lending/pom.xml module-lending/
COPY module-notification/pom.xml module-notification/
COPY module-reminding/pom.xml module-reminding/
COPY module-user/pom.xml module-user/
COPY schoollibrary-app/pom.xml schoollibrary-app/

# Fetch dependencies (this will be cached if poms don't change)
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY . .
RUN mvn clean package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create a non-root user and logs directory
RUN addgroup -S spring && adduser -S spring -G spring
RUN mkdir -p logs && chown spring:spring logs
USER spring:spring

# Copy the jar from the build stage
COPY --from=build /app/schoollibrary-app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
