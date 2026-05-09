# Insighta Labs API + Platform

A full-stack intelligent profile classification platform built with Spring Boot, React, JWT authentication, GitHub OAuth, CLI tooling, CI/CD, and cloud deployment.

---

# Live Links

## Frontend

```text
insighta-web-production-6e8a.up.railway.app
```

## Backend API

```text
hng-practise-production.up.railway.app
```

## Frontend Repository

```text
https://github.com/Dave48den/insighta-web
```

## Backend Repository

```text
https://github.com/Dave48den/hng-practise
```

---

# Features

## Authentication & Security

* GitHub OAuth Login
* JWT Authentication
* Refresh Tokens
* Role-Based Access Control (RBAC)
* Protected Routes
* API Versioning
* Request Rate Limiting
* Secure Authorization Headers

## Profile Intelligence

* Gender Prediction using Genderize API
* Age Prediction using Agify API
* Nationality Prediction using Nationalize API
* UUID v7 Profile IDs
* Country Name Resolution
* Intelligent Classification Pipeline

## Search & Filtering

* Pagination
* Sorting
* Advanced Filtering
* Natural Language Query Parsing
* Probability Filtering
* Dynamic Search

## CLI Features

* GitHub Login
* Profile Listing
* CSV Export
* Authenticated Requests
* Local Token Storage

## Frontend Features

* React + Vite Frontend
* Protected Dashboard
* Profiles Page
* Account Page
* GitHub OAuth Flow
* Persistent Login Session

## DevOps

* Railway Deployment
* GitHub Actions CI/CD
* Automated Frontend Builds
* Automated Backend Builds

---

# Tech Stack

## Backend

* Java 21
* Spring Boot 3
* Spring Security
* Spring Data JPA
* MySQL
* JWT
* Maven

## Frontend

* React
* Vite
* Axios
* React Router DOM

## Infrastructure

* Railway
* GitHub Actions
* GitHub OAuth

---

# System Architecture

```text
React Frontend
        ↓
Spring Boot API
        ↓
Authentication Layer
        ↓
Business Logic Layer
        ↓
External Intelligence APIs
        ↓
MySQL Database
```

---

# Authentication Flow

```text
User clicks GitHub Login
        ↓
GitHub OAuth Authorization
        ↓
Backend receives OAuth code
        ↓
Backend exchanges code for GitHub user
        ↓
JWT Access + Refresh Tokens generated
        ↓
Frontend stores access token
        ↓
Protected routes become accessible
```

---

# API Endpoints

## Authentication

### GitHub OAuth Callback

```http
GET /auth/github/callback
```

### Refresh Token

```http
POST /auth/refresh
```

---

## Profiles

### Get Profiles

```http
GET /api/profiles
```

### Create Profile

```http
POST /api/profiles
```

### Export Profiles

```http
GET /api/profiles/export
```

### Natural Language Search

```http
GET /api/profiles/search
```

---

# Sample Request

```http
GET /api/profiles?gender=female&page=1&limit=10
```

---

# Sample Response

```json
{
  "status": "success",
  "page": 1,
  "limit": 10,
  "total": 50,
  "total_pages": 5,
  "data": [
    {
      "id": "uuid-v7",
      "name": "Amina",
      "gender": "female",
      "age": 24,
      "countryName": "Nigeria"
    }
  ]
}
```

---

# Environment Variables

## Backend

```env
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
JWT_SECRET=
GITHUB_CLIENT_ID=
GITHUB_CLIENT_SECRET=
```

## Frontend

```env
VITE_API_URL=
```

---

# Local Development Setup

## Backend Setup

```bash
git clone YOUR_BACKEND_REPO
cd hng-practise
mvn spring-boot:run
```

---

## Frontend Setup

```bash
git clone YOUR_FRONTEND_REPO
cd insighta-web
npm install
npm run dev
```

---

# CLI Setup

## Install Dependencies

```bash
npm install
```

## Run Login

```bash
insighta login
```

## List Profiles

```bash
insighta profiles:list
```

## Export Profiles

```bash
insighta export
```

---

# CI/CD

## Frontend CI

GitHub Actions automatically:

* Installs dependencies
* Builds the React application
* Verifies deployment readiness

## Backend CI

GitHub Actions automatically:

* Builds Spring Boot application
* Runs Maven lifecycle
* Verifies backend integrity

---

# Security Measures

* JWT Signature Validation
* Refresh Token Rotation
* Protected Endpoints
* Role-Based Authorization
* Secure OAuth Flow
* CORS Protection
* Rate Limiting

---

# Deployment

## Frontend Deployment

Hosted on Railway.

## Backend Deployment

Hosted on Railway.

## Database

MySQL hosted on Railway.

---

# Future Improvements

* Admin Analytics Dashboard
* Redis Caching
* Swagger/OpenAPI Documentation
* Email Notifications
* Docker Support
* Kubernetes Deployment
* AI Recommendation Engine

---

# Author

## King David

Backend Engineer | Cybersecurity Student | Cloud Computing Enthusiast

GitHub:

```text
https://github.com/Dave48den
```

---

# License

MIT License
