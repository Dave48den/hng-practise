# Backend Wizards Stage 2 API

##  Overview
This project is a Spring Boot REST API that processes user names, enriches them using external APIs (Genderize, Agify, Nationalize), stores results in a database, and provides advanced search capabilities including filtering, pagination, sorting, and natural language query parsing.

---

##  Features

- Name classification using external APIs
- Gender prediction (Genderize API)
- Age prediction (Agify API)
- Nationality prediction (Nationalize API)
- Data persistence using database
- Advanced filtering system
- Pagination with limit enforcement (max 50)
- Sorting by multiple fields
- Natural language query parsing (e.g. "young males from kenya")

---

##  Base URL
https://hng-practise-production.up.railway.app

---

##  API Endpoints

### 1. Create Profile
**POST** `/api/classify`

Request:
{
  "name": "John"
}

Response:
{
  "id": "uuid",
  "name": "John",
  "gender": "male",
  "genderProbability": 0.91,
  "age": 24,
  "ageGroup": "adult",
  "countryName": "Ghana",
  "countryProbability": 0.87
}

---

### 2. Get Profiles (Filtering + Pagination + Sorting)
**GET** `/api/profiles`

Query Params:
- gender
- age_group
- country_id
- min_age
- max_age
- min_gender_probability
- min_country_probability
- sort_by
- order
- page
- limit (max 50)

Example:
/api/profiles?gender=male&sort_by=name&order=asc&page=1&limit=10

---

### 3. Natural Language Search
**GET** `/api/profiles/search`

Examples:
/api/profiles/search?q=young males
/api/profiles/search?q=adult females from ghana
/api/profiles/search?q=teenagers above 17

---

##  Query Interpretation

| Input | Meaning |
|------|--------|
| young males | age 0–17 + male |
| adult females from kenya | age 20–59 + female + KE |
| teenagers above 17 | age ≥ 17 |

---

##  Tech Stack

- Java 17+
- Spring Boot
- Spring Data JPA
- PostgreSQL / MySQL
- REST APIs

---

##  Deployment
https://hng-practise-production.up.railway.app

---

## 👨‍💻 Author
Backend Wizards Stage 2 Submission
