Nutrisaas Core - Generated skeleton

This project contains the 'core' module skeleton for the Nutrisaas product.
It includes:
- Spring Boot application
- JPA entities: User, Role, Permission
- Repositories
- JWT-based security (TokenProvider, JwtFilter)
- AuthController with /auth/login
- UserService with a helper to register a nutritionist

How to run:
1. Configure src/main/resources/application.properties with your PostgreSQL credentials and a secure app.jwt.secret
2. Build with: mvn clean package
3. Run: mvn spring-boot:run

Note: This is a starter skeleton. Add more modules (patients, plans, appointments) following the package structure:
com.nutrisaas.core
com.nutrisaas.patients
com.nutrisaas.appointments
com.nutrisaas.plans
