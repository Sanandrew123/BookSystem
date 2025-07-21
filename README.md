# 📚 Library Management System

A modern, full-stack Java-based **Library Management System** designed as a university **graduation project**. The system adopts a **frontend-backend separation architecture** with a clean, scalable, and user-friendly design.

## ✨ Features

### 🔙 Backend
- **Spring Boot 3.x** with Java 17+
- **RESTful APIs** with comprehensive CRUD operations
- **JWT Authentication** with role-based access control
- **MySQL Database** with JPA/Hibernate ORM
- **Swagger UI** for API documentation and testing
- **Docker support** for containerized deployment

### 🎨 Frontend  
- **Vue.js 3** with TypeScript support
- **Modern UI/UX** with Tailwind CSS
- **Responsive design** optimized for all devices
- **Premium typography** using Inter and Lora fonts
- **Professional color scheme** with warm tones
- **Real-time search** and advanced filtering

### ⚙️ Core Functionality
- 📚 **Book Management**: Complete CRUD operations for library catalog
- 🔄 **Borrowing System**: Seamless book borrowing and return workflow  
- 👥 **User Management**: Role-based access (Admin, Librarian, Student)
- 🔍 **Advanced Search**: Multi-criteria filtering and smart search
- 🔐 **Secure Authentication**: JWT-based login with password encryption
- 📊 **Analytics Dashboard**: Statistics and borrowing insights
- 🧾 **Activity Logging**: Comprehensive audit trail and history

## 🎯 User Roles

### 👨‍💼 Admin
- Full system access and user management
- Book inventory management
- System configuration and statistics
- User role assignment and permissions

### 👩‍🏫 Librarian  
- Book catalog management (add, edit, update inventory)
- Borrowing and return processing
- Overdue book management and fine calculation
- Student borrowing history and reports

### 👨‍🎓 Student
- Browse and search library catalog
- Borrow and return books
- View personal borrowing history
- Renew borrowed books (when eligible)

## 🚀 Quick Start

### Prerequisites
- **Java 17+**
- **Node.js 18+**
- **MySQL 8.0+**
- **Docker & Docker Compose** (for containerized deployment)

### 🐳 Docker Deployment (Recommended)

1. **Clone the repository**
   ```bash
   git clone https://github.com/Sanandrew123/BookSystem.git
   cd BookSystem
   ```

2. **Start with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the application**
   - Frontend: http://localhost
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

### 🛠️ Manual Development Setup

#### Backend Setup
```bash
cd backend
./mvnw clean install
./mvnw spring-boot:run
```

#### Frontend Setup  
```bash
cd frontend
npm install
npm run dev
```

#### Database Setup
1. Create MySQL database: `library_db`
2. Update `application.yml` with your database credentials
3. Tables will be created automatically on first run

## 🔐 Default Login Credentials

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| Admin | `admin` | `password` | Full system access |
| Librarian | `librarian` | `password` | Book and borrowing management |
| Student | `student1` | `password` | Book browsing and borrowing |
| Student | `student2` | `password` | Book browsing and borrowing |

## 📁 Project Structure

```
library-management-system/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/library/management/
│   │       ├── controller/  # REST controllers
│   │       ├── service/     # Business logic
│   │       ├── repository/  # Data access layer
│   │       ├── entity/      # JPA entities
│   │       ├── dto/         # Data transfer objects
│   │       ├── config/      # Configuration classes
│   │       └── security/    # JWT and security config
│   ├── src/main/resources/
│   │   └── application.yml  # Application configuration
│   └── Dockerfile
│
├── frontend/                # Vue.js application
│   ├── src/
│   │   ├── components/      # Reusable Vue components
│   │   ├── views/           # Page components
│   │   ├── stores/          # Pinia state management
│   │   ├── router/          # Vue Router configuration
│   │   ├── types/           # TypeScript type definitions
│   │   └── assets/          # Static assets and styles
│   ├── public/              # Public static files
│   └── Dockerfile
│
├── docker-compose.yml       # Multi-container orchestration
├── init.sql                 # Database initialization
└── README.md               # Project documentation
```

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration  
- `GET /api/auth/me` - Get current user profile

### Books
- `GET /api/books` - List books with pagination and filtering
- `GET /api/books/{id}` - Get book by ID
- `POST /api/books` - Create new book (Librarian+)
- `PUT /api/books/{id}` - Update book (Librarian+)
- `DELETE /api/books/{id}` - Delete book (Admin only)

### Borrowing
- `GET /api/borrows/my-books` - User's borrowing history
- `POST /api/borrows/borrow` - Borrow a book
- `POST /api/borrows/{id}/return` - Return a book
- `POST /api/borrows/{id}/renew` - Renew borrowed book

### Admin
- `GET /api/admin/users` - List all users (Admin only)
- `POST /api/admin/users` - Create user (Admin only)
- `GET /api/dashboard/stats` - System statistics

## 🎨 Design Philosophy

This application embraces modern design principles with:

- **Warm Color Palette**: Professional oranges and earth tones (avoiding typical blue schemes)
- **Premium Typography**: Inter for interface text, Lora serif for headings
- **Minimalist Layout**: Clean, spacious design inspired by Goodreads and Behance
- **Responsive Design**: Mobile-first approach with Tailwind CSS
- **Accessibility**: High contrast ratios and keyboard navigation support

## 🔧 Configuration

### Backend Configuration (`application.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db
    username: your_username
    password: your_password
  
jwt:
  secret: your_jwt_secret_key
  expiration: 86400000  # 24 hours
```

### Frontend Configuration
Update API base URL in `src/stores/auth.ts` if needed:
```typescript
const API_URL = 'http://localhost:8080/api'
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend  
npm run test
```

## 📦 Production Build

### Backend
```bash
cd backend
./mvnw clean package -Pprod
```

### Frontend
```bash
cd frontend
npm run build
```

## 🐳 Docker Commands

```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services  
docker-compose down

# Rebuild containers
docker-compose up -d --build

# Reset database (removes all data)
docker-compose down -v
docker-compose up -d
```

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🎓 Academic Usage

This project is designed as a **university graduation project** demonstrating:

- **Full-stack development** skills with modern technologies
- **Software architecture** and design patterns
- **Database design** and optimization
- **Security implementation** with JWT authentication
- **API design** following REST principles
- **Modern UI/UX** development practices
- **DevOps practices** with Docker containerization

Perfect for showcasing professional development capabilities in academic and professional contexts.

## 📞 Support

For questions, issues, or contributions:

- 📧 Email: [admin@library.com](mailto:admin@library.com)
- 🐛 Issues: [GitHub Issues](https://github.com/Sanandrew123/BookSystem/issues)
- 📖 Documentation: [API Documentation](http://localhost:8080/swagger-ui.html)

---

> ✅ **Production Ready**: This system is fully functional and ready for real-world deployment with proper security, scalability, and maintainability considerations.

**Built with ❤️ using modern technologies and best practices.**