# 🐰 RabbitMQ Demo - Spring Boot Application

A demonstration project showcasing **RabbitMQ** message queue integration with **Spring Boot** using the native AMQP client library.

---

## 📋 Project Overview

This project demonstrates a simple producer-consumer pattern using RabbitMQ. It allows you to send product events via REST API, which are then published to a RabbitMQ queue and consumed asynchronously.

### Key Features
- ✅ RabbitMQ integration using native AMQP client
- ✅ REST API for publishing messages
- ✅ Asynchronous message consumption
- ✅ Docker Compose setup for RabbitMQ
- ✅ JSON serialization/deserialization
- ✅ Producer-Consumer pattern implementation

---

## 🏗️ Architecture

```
REST API (Controller)
    ↓
Producer (publishes to queue)
    ↓
RabbitMQ Queue (products_queue)
    ↓
Consumer (consumes from queue)
```

### Components

| Component | Description |
|-----------|-------------|
| **ProductController** | REST endpoint to trigger message publishing |
| **ProductProducer** | Publishes ProductEvent messages to RabbitMQ |
| **ProductConsumer** | Consumes and processes messages from queue |
| **ProductEvent** | Message payload (DTO) |
| **RabbitMQConnectionConfig** | RabbitMQ connection configuration |

---

## 💻 Technology Stack

- **Java**: 21
- **Spring Boot**: 4.0.2
- **RabbitMQ Client**: 5.28.0
- **Jackson**: 2.17.2 (JSON processing)
- **Lombok**: For boilerplate reduction
- **Docker**: For RabbitMQ containerization
- **Maven**: Build tool

---

## 📁 Project Structure

```
com.rabbitmq.raw
 ├── config/
 │   └── RabbitMQConnectionConfig.java    # RabbitMQ connection setup
 ├── controller/
 │   └── ProductController.java           # REST API endpoints
 ├── model/
 │   └── ProductEvent.java                # Message DTO
 ├── producer/
 │   └── ProductProducer.java             # Message publisher
 ├── consumer/
 │   └── ProductConsumer.java             # Message consumer
 └── RabbitRawApplication.java            # Main application class
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or higher
- **Maven 3.6+**
- **Docker & Docker Compose** (for RabbitMQ)

### 1. Start RabbitMQ

Using Docker Compose:

```bash
docker-compose up -d
```

This will start RabbitMQ with:
- **AMQP Port**: 5672
- **Management UI**: http://localhost:15672
- **Username**: user
- **Password**: user123

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

Or using the Maven wrapper:

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

---

## 📡 API Endpoints

### Add Product (Publish Message)

**Endpoint**: `POST /products/add/{name}`

**Example**:
```bash
curl -X POST http://localhost:8080/products/add/Laptop
```

**Response**:
```
Product message sent to RabbitMQ
```

**What happens**:
1. REST endpoint receives product name
2. Creates a `ProductEvent` with action "CREATED"
3. Publishes message to `products_queue`
4. Consumer picks up and processes the message

---

## 🔧 Configuration

### RabbitMQ Connection

Located in `RabbitMQConnectionConfig.java`:

```java
Host: localhost
Port: 5672
Username: user
Password: user123
```

### Queue Configuration

- **Queue Name**: `products_queue`
- **Durable**: `true`
- **Exclusive**: `false`
- **Auto-delete**: `false`

---

## 🧪 Testing

### 1. Check RabbitMQ is Running

Access the management UI at: http://localhost:15672

Login with:
- Username: `user`
- Password: `user123`

### 2. Send a Test Message

```bash
curl -X POST http://localhost:8080/products/add/TestProduct
```

### 3. Check Console Output

You should see in the application console:
```
📩 Received:
TestProduct
CREATED
```

### 4. Verify in RabbitMQ UI

- Navigate to "Queues" tab
- Check `products_queue` statistics
- View message rates and counts

---

## 📦 Dependencies

```xml
<!-- Spring Boot Web MVC -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- RabbitMQ AMQP Client -->
<dependency>
    <groupId>com.rabbitmq</groupId>
    <artifactId>amqp-client</artifactId>
    <version>5.28.0</version>
</dependency>

<!-- Jackson for JSON -->
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.17.2</version>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```

---

## 🎯 How It Works

### Publishing Flow

1. **HTTP Request** arrives at `ProductController`
2. **ProductEvent** object is created with product name and action
3. **ProductProducer** serializes the event to JSON
4. Message is **published** to `products_queue`
5. Success response returned to client

### Consuming Flow

1. **ProductConsumer** starts on application startup (`@PostConstruct`)
2. Listens to `products_queue` in a separate thread
3. On message arrival, **deserializes** JSON to `ProductEvent`
4. **Processes** the event (currently prints to console)
5. Auto-acknowledges the message

---

## 🐳 Docker Configuration

The `docker-compose.yml` provides a complete RabbitMQ setup:

```yaml
services:
  rabbitmq:
    image: rabbitmq:3-management
    ports:
      - "5672:5672"      # AMQP protocol
      - "15672:15672"    # Management UI
    environment:
      RABBITMQ_DEFAULT_USER: user
      RABBITMQ_DEFAULT_PASS: user123
```

---

## 🛠️ Development

### Adding New Message Types

1. Create a new model class in `model/` package
2. Create corresponding producer in `producer/` package
3. Create corresponding consumer in `consumer/` package
4. Add controller endpoint to trigger publishing

### Example Structure

```java
// Model
@Data
public class OrderEvent implements Serializable {
    private String orderId;
    private String status;
}

// Producer
@Service
public class OrderProducer {
    private static final String QUEUE_NAME = "orders_queue";
    // Implementation...
}

// Consumer
@Component
public class OrderConsumer {
    // Implementation...
}
```

---

## 📝 Notes

- The consumer runs in a **separate thread** to avoid blocking the main application
- Messages are **auto-acknowledged** (consider manual acknowledgment for production)
- Queue is **durable** - survives RabbitMQ restarts
- Uses **default exchange** (direct routing by queue name)

---

## 🔍 Troubleshooting

### Connection Refused

**Issue**: Can't connect to RabbitMQ

**Solution**: 
- Ensure Docker container is running: `docker ps`
- Check RabbitMQ logs: `docker logs rabbitmq`

### Messages Not Being Consumed

**Issue**: Producer works but consumer doesn't receive messages

**Solution**:
- Check application logs for consumer startup
- Verify queue exists in RabbitMQ management UI
- Ensure queue names match in producer and consumer

### Port Already in Use

**Issue**: Port 5672 or 15672 already in use

**Solution**:
- Stop existing RabbitMQ instance
- Or modify ports in `docker-compose.yml`

---

## 📚 Additional Resources

- [RabbitMQ Official Documentation](https://www.rabbitmq.com/documentation.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [AMQP Client Library](https://www.rabbitmq.com/java-client.html)

---

## 📄 License

This is a demo project for learning purposes.

---

## 👤 Author

Created as a demonstration of RabbitMQ integration with Spring Boot.

---

## 🎓 Learning Outcomes

After working with this project, you will understand:

- ✅ How to set up RabbitMQ with Docker
- ✅ Producer-Consumer pattern implementation
- ✅ RabbitMQ native client usage
- ✅ Message serialization/deserialization
- ✅ Asynchronous message processing
- ✅ Queue declaration and management

---

**Happy Messaging! 🚀**
