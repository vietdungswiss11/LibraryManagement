# BookWeb - Online Bookstore

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/JWT-Auth-yellow)](https://jwt.io/)

A full-featured online bookstore built with Spring Boot and React.

## ✨ Features

### 📚 Books
- Browse books with filters and sorting
- Search by title, author, category
- Book details with reviews
- Admin CRUD operations

### 🛒 Shopping
- Shopping cart
- Online payment (VNPay)
- Order tracking
- Discount codes

### 👤 Account
- Register/Login
- Google OAuth2 login
- Profile management
- Order history
- Product reviews

## 🛠 Tech Stack

**Backend**
- Spring Boot 3.5.0, Java 21
- Spring Security + JWT
- MySQL 8.0, JPA/Hibernate
- Cloudinary (image storage)
- VNPay integration
- Spring Mail

## 🚀 Quick Start

### Prerequisites
- Java 21+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+

### Backend Setup
1. Clone the repo
2. Configure `application.properties`
3. Run: 
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup
```bash
cd frontend
npm install
npm start
```

## 🔧 Configuration
Create `.env` file:
```properties
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/bookweb?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password

# JWT
JWT_SECRET=your-secret-key

# Cloudinary (optional)
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

## 📚 API Docs
Access at: `http://localhost:8080/swagger-ui.html`

## 📝 License
[MIT](LICENSE) © 2025