# 📚 Library Management System

A modern, full-stack Java-based **Library Management System** designed as a university **graduation project**. The system adopts a **frontend-backend separation architecture** with a clean, scalable, and user-friendly design.

## 🔙 Backend
- Built with **Spring Boot**
- Exposes **RESTful APIs** for frontend consumption
- Implements **JWT (JSON Web Token)** based authentication and role-based access control
- Connects to **MySQL** database using **JPA (Java Persistence API) / Hibernate**
- Includes Swagger for API documentation and optional Docker support

## 🎨 Frontend
- Built using **Vue.js** with a focus on modern UI/UX principles
- Responsive layout with dynamic dashboard components
- Integrates **Axios** for API communication
- Inspired by the aesthetics of leading platforms like [Goodreads](https://www.goodreads.com/) and [Behance](https://www.behance.net/), with careful attention to typography, spacing, and color schemes
- Uses **premium fonts** (e.g., Google Fonts like "Poppins", "Lora", or "Inter") for elegant and professional readability

## ⚙️ Features
- 📚 Book catalog management (CRUD)
- 🔄 Borrowing and returning workflow
- 👥 Role-based access (Admin, Librarian, Student)
- 🔍 Smart search and multi-condition filtering
- 🔐 Secure login and registration
- 🧾 Operation logging and audit

## 🎯 Design Goals
- Clean and professional user experience
- Minimalist, elegant, and modern layout
- Easy to maintain and extend

## 🗂️ Technologies
- Java 17+, Spring Boot 3.x
- REST API, JWT (JSON Web Token), Spring Security
- MySQL, JPA/Hibernate
- Vue 3, Axios
- Swagger UI, Docker (optional)
- Git & GitHub for version control and progress tracking

## 🚀 Getting Started

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## 📁 Project Structure
```
library-management-system/
├── backend/           # Spring Boot application
├── frontend/          # Vue.js application
├── docker-compose.yml # Docker configuration
└── README.md          # Project documentation
```

---

> ✅ All development progress is tracked and versioned using **GitHub**. Each major feature is committed in an isolated branch to ensure clarity and rollback safety.

Ideal for academic presentation, real-world usability, and demonstrating professional full-stack Java development skills.