FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

# Copiar todo el proyecto
COPY . .

# Dar permisos de ejecución al Maven Wrapper
RUN chmod +x mvnw

# Build del proyecto
RUN ./mvnw clean package -DskipTests

# Exponer puerto (Render usa $PORT)
EXPOSE 8080

# Ejecutar la app
CMD ["java", "-jar", "target/nutrisaas-0.0.1-SNAPSHOT.jar"]
