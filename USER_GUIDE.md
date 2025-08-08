# Electronic Store Microservices - User Guide

## 🌟 System Overview
Welcome to the Electronic Store application! This is a modern, event-driven microservices architecture that powers an e-commerce platform with real-time capabilities.

## 🚀 Key Features
- **User Management**: Create and manage user accounts
- **Product Catalog**: Browse and search products
- **Shopping Cart**: Add/remove items before checkout
- **Order Processing**: Place and track orders in real-time
- **Inventory Management**: Real-time stock tracking
- **Notifications**: Get email and SMS updates about your orders
- **Payments**: Secure payment processing
- **Shipping**: Track your order delivery

## 🔄 How It Works
1. **User Registration**
   - Create an account
   - Receive welcome email/SMS
   - Browse products

2. **Shopping Experience**
   - Add items to cart
   - View cart contents
   - Proceed to checkout

3. **Order Processing**
   - Place order
   - Get order confirmation
   - Real-time status updates
   - Shipping notifications

## 📱 Notification System
- **Email Notifications**:
  - Welcome emails
  - Order confirmations
  - Shipping updates
  - Promotional offers

- **SMS Notifications**:
  - Order confirmations
  - Shipping updates
  - Delivery alerts

## 🛠️ Technical Highlights
- **Event-Driven Architecture**: Real-time updates using Kafka
- **Microservices**: Independent, scalable services
- **Resilient Design**: Automatic retries and error handling
- **Performance Optimized**: Caching and efficient data access

## 📊 System Architecture
```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  User Service   │     │  Cart Service   │     │ Product Service │
└────────┬────────┘     └────────┬────────┘     └────────┬────────┘
         │                       │                       │
         └───────────┬───────────┘                       │
                     │                                   │
                     ▼                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                         KAFKA EVENT BUS                         │
└─────────────────────────────────────────────────────────────────┘
                     ▲                                   ▲
                     │                                   │
         ┌───────────┴───────────┐     ┌────────────────┴────────────────┐
         │                       │     │                                 │
┌────────▼────────┐    ┌─────────▼────────┐    ┌─────────────────────────▼────────────────────────┐
│  Order Service  │    │  Email Service   │    │                SMS Notification                │
└─────────────────┘    └──────────────────┘    └────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────┐    ┌─────────────────┐
│ Payment Service │    │ Shipping Service│
└─────────────────┘    └─────────────────┘
```

## 🔍 Quick Start
1. **Prerequisites**:
   - Docker and Docker Compose
   - Java 17+
   - Maven

2. **Starting Services**:
   ```bash
   # Start Kafka and dependencies
   docker-compose up -d
   
   # Build and start all microservices
   mvn clean install
   ```

## 📞 Support
For any issues or questions, please contact our support team at support@electronicstore.com

## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
