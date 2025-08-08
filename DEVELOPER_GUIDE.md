# Developer Setup Guide

## 🛠️ Prerequisites

### System Requirements
- Java 17 or higher
- Maven 3.6.3 or higher
- Docker 20.10.0 or higher
- Docker Compose 1.29.0 or higher
- Git
- IDE (IntelliJ IDEA, VS Code, or Eclipse)

### Environment Variables
Create a `.env` file in the project root with the following variables:

```env
# Kafka Configuration
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/estore
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres

# Email Configuration (for email-notification-service)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-specific-password

# Twilio Configuration (for sms-notification-service)
TWILIO_ACCOUNT_SID=your-account-sid
TWILIO_AUTH_TOKEN=your-auth-token
TWILIO_PHONE_NUMBER=+1234567890
```

## 🚀 Local Development Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/Store-With-Kafka.git
cd Store-With-Kafka
```

### 2. Start Infrastructure
```bash
# Start Kafka, Zookeeper, and Kafka UI
docker-compose up -d
```

### 3. Build the Project
```bash
# Build all services
mvn clean install -DskipTests
```

### 4. Start Services
Run these commands in separate terminal windows:

```bash
# Start Eureka Server (if applicable)
# Start Config Server (if applicable)

# Start Core Services
cd user-service && mvn spring-boot:run
cd product-service && mvn spring-boot:run
cd cart-service && mvn spring-boot:run
cd order-service && mvn spring-boot:run

# Start Support Services
cd inventory-service && mvn spring-boot:run
cd payment-service && mvn spring-boot:run
cd shipping-service && mvn spring-boot:run

# Start Notification Services
cd email-notification-service && mvn spring-boot:run
cd sms-notification-service && mvn spring-boot:run
```

## 🧪 Running Tests

### Unit Tests
```bash
# Run tests for all services
mvn test

# Run tests for a specific service
cd service-name && mvn test
```

### Integration Tests
```bash
# Run integration tests with testcontainers
mvn verify -Pintegration-test
```

## 📊 Monitoring and Debugging

### Accessing Services
- **Kafka UI**: http://localhost:8080
- **Eureka Dashboard**: http://localhost:8761 (if enabled)
- **Actuator Endpoints**: http://localhost:8080/actuator (for each service)

### Viewing Logs
```bash
# View logs for a specific service
docker-compose logs -f service-name

# View Kafka logs
docker-compose logs -f kafka
```

## 🔧 Common Issues and Solutions

### 1. Port Conflicts
If you encounter port conflicts, check which service is using the port:
```bash
# On Linux/Mac
lsof -i :8080

# On Windows
netstat -ano | findstr :8080
```

### 2. Kafka Connection Issues
If services can't connect to Kafka:
1. Ensure Docker is running
2. Check if all containers are up: `docker ps`
3. Check Kafka logs: `docker-compose logs -f kafka`

### 3. Database Connection Issues
- Verify PostgreSQL is running
- Check database credentials in application.properties
- Run database migrations if needed

## 🛠️ IDE Setup

### IntelliJ IDEA
1. Install Lombok plugin
2. Enable annotation processing
3. Set up Spring Boot run configurations for each service

### VS Code
1. Install Java Extension Pack
2. Install Spring Boot Extension Pack
3. Install Lombok Annotations Support

## 📚 Additional Resources
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Docker Documentation](https://docs.docker.com/)

## 🤝 Contributing
1. Create a new branch: `git checkout -b feature/your-feature`
2. Make your changes
3. Run tests: `mvn clean test`
4. Commit your changes: `git commit -m "Add your feature"`
5. Push to the branch: `git push origin feature/your-feature`
6. Create a Pull Request

## 📞 Support
For any issues or questions, please contact the development team or create an issue in the repository.
