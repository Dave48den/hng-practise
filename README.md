Backend Wizards Stage 2 API
Overview
This API provides advanced demographic data management for Insighta Labs. It supports high-performance filtering, sorting, and a custom rule-based Natural Language Processing (NLP) engine to query profiles using plain English.
API Endpoints
1. Get All Profiles
GET /api/profiles
Supports combined filters, pagination, and sorting.
Filters: gender, age_group, country_id, min_age, max_age, min_gender_probability, min_country_probability.
Sorting: sort_by (age, created_at, gender_probability), order (asc, desc).
Pagination: page (default 1), limit (default 10, max 50).
2. Natural Language Query
GET /api/profiles/search?q={query}
Parses plain English into structured database filters.
Natural Language Parsing Approach
Our parser uses a rule-based Regex engine (no LLMs) to map keywords to database filters:
Keyword Mapping:
Gender: Detects male or female.
Age Groups: Detects child, teenager, adult, or senior.
Age-Specific: "Young" is strictly mapped to the 16–24 age range as per requirements.
Comparison Logic:
Uses Regex to identify the words above, over, below, or under followed by a number.
above/over → Sets min_age.
below/under → Sets max_age.
Location Logic:
Detects country names (e.g., "Kenya", "Nigeria") and maps them to ISO codes (KE, NG) using a predefined dictionary.
Combination Logic:
Keywords are additive. For example, "young males from nigeria" combines gender=male, min_age=16, max_age=24, and country_id=NG.
Query Example	Internal Mapping
"young males"	gender: male, min_age: 16, max_age: 24
"females above 30"	gender: female, min_age: 30
"adult males from kenya"	gender: male, age_group: adult, country_id: KE
"teenagers above 17"	age_group: teenager, min_age: 17
Limitations
The current parser is built for speed and specific business rules but has the following limitations:
No Complex Conjunctions: Does not support OR or AND NOT logic (e.g., "males but not from Kenya").
Single Country Only: Can only parse one country per query.
Specific Phrasing: Comparison logic requires standard phrasing like "above [number]". Slang or unusual sentence structures may result in an "Unable to interpret query" error.
Fixed Thresholds: The definition of "young" is hardcoded to 16–24 and does not adapt to different cultural contexts.
Tech Stack
Language: Java 21
Framework: Spring Boot 3.3.5
Database: PostgreSQL (with UUID v7 primary keys)
Deployment: Railway
