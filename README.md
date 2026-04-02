# Customer Management System

A full-stack web application for managing customer records with pagination, sorting, and validation.

## 🚀 Quick Start (2 minutes)

### 1. Start Backend
```bash
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### 2. Start Frontend
```bash
cd frontend
npm install
npm start
```
Frontend runs on `http://localhost:3000`

### 3. Open Browser
Visit `http://localhost:3000` and start adding customers!

## 📋 Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.8+

## 🛠️ Tech Stack

**Backend:** Java 17, Spring Boot 3.5, H2 Database, Maven  
**Frontend:** React 19, TypeScript, CSS  
**Testing:** JUnit 5, Jest, Playwright

## ✨ Features

- ✅ Create customers with validation
- ✅ Paginated list (5 customers per page)
- ✅ Sort by First Name or Last Name (click column headers)
- ✅ Client & server-side validation
- ✅ Responsive design

## 🧪 Testing

### Backend Tests
```bash
./mvnw test
```

### Frontend Unit Tests
```bash
cd frontend
npm test
```

### E2E Tests (Playwright)
```bash
# Terminal 1: Start backend
./mvnw spring-boot:run

# Terminal 2: Run E2E tests
cd frontend
npx playwright install    # one-time setup
npx playwright test
npx playwright test --ui  # interactive mode
```

## 📁 Project Structure

```
customer-management/
├── src/main/java/              # Backend (Spring Boot)
│   ├── controller/             # REST endpoints
│   ├── service/                # Business logic
│   ├── repository/             # Data access
│   ├── dto/                    # Request/Response objects
│   └── entity/                 # Database entities
├── frontend/
│   ├── src/
│   │   ├── components/         # React components
│   │   ├── services/           # API client
│   │   └── types/              # TypeScript types
│   └── e2e/                    # Playwright tests
└── README.md
```

## 🔌 API Endpoints

### Create Customer
```http
POST /api/v1/customers
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-15"
}
```

### Get Customers (Paginated & Sorted)
```http
GET /api/v1/customers?page=0&size=5&sort=lastName,asc
```

**Response:**
```json
{
  "customers": [...],
  "page": 0,
  "size": 5,
  "totalElements": 10,
  "totalPages": 2
}
```

## ✅ Validation Rules

**First Name & Last Name:**
- Required
- 1-50 characters
- Only letters, spaces, hyphens, apostrophes

**Date of Birth:**
- Required
- Must be in the past

## 🔧 Troubleshooting

**Port 8080 already in use:**
```bash
# Find and kill the process
netstat -ano | findstr :8080    # Windows
lsof -i :8080                   # Mac/Linux
```

**Frontend can't connect to backend:**
- Ensure backend is running on `http://localhost:8080`
- Check `frontend/package.json` has `"proxy": "http://localhost:8080"`

**E2E tests failing:**
- Make sure backend is running first
- Run `npx playwright install` if browsers aren't installed

## 📚 Additional Documentation

- `frontend/E2E_TEST_INSTRUCTIONS.md` - Detailed E2E testing guide
- Swagger UI: `http://localhost:8080/swagger-ui.html` (when backend is running)

## 🔄 API Versioning

- **Current Version:** v1 (`/api/v1/customers`)
- **Breaking changes** will increment version (v2, v3)
- **Old versions** supported for 6 months after new release

## 💾 Database

- **H2 in-memory database** (data resets on restart)
- H2 Console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:testdb`
  - Username: `sa`
  - Password: (empty)

## 📝 License

Apache License 2.0
