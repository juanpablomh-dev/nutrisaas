FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY . .

# Dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Build del proyecto
RUN ./mvnw clean package -DskipTests

EXPOSE 8080
CMD ["java", "-jar", "target/nutrisaas-0.0.1-SNAPSHOT.jar"]
