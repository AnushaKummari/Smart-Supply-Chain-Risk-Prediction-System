# SSRP System Architecture
AI-Powered Smart Supply Chain Risk Prediction System

---

# 1. Architecture Overview

The Smart Supply Chain Risk Prediction System (SSRP) follows a **microservice-oriented architecture** where different components handle specialized responsibilities.

The architecture separates:

- Frontend (User Interface)
- Backend API layer
- AI/ML prediction service
- Database
- Real-time alert system

This modular architecture ensures:

- scalability
- maintainability
- independent service deployment
- easier AI integration

---

# 2. High-Level Architecture

                 +----------------------+
                 |      React UI        |
                 |  Dashboard + Pages   |
                 +----------+-----------+
                            |
                            | REST API
                            |
                +-----------v-----------+
                |    Spring Boot API    |
                |   Business Logic      |
                +-----------+-----------+
                            |
                +-----------+-----------+
                |                       |
                |                       |
        +-------v-------+       +-------v-------+
        |  Python ML    |       |  PostgreSQL   |
        | Prediction    |       |   Database    |
        | Microservice  |       |               |
        +---------------+       +---------------+

---

# 3. Technology Stack

Frontend

React.js  
Axios  
Chart.js / Recharts  
React Router

Backend

Java 21  
Spring Boot  
Spring Security  
Spring Data JPA  
JWT Authentication

AI/ML Service

Python  
Flask / FastAPI  
Scikit-learn  
XGBoost  
Pandas

Database

PostgreSQL

Database Client

SQL Workbench

Deployment

Docker Desktop  
Docker Compose

---

# 4. Service Components

## 4.1 React Frontend

Responsibilities

- User interface
- Dashboard visualization
- Shipment management
- Alert display
- Analytics charts
- Supplier risk view
- Inventory monitoring

Main Pages

- Login Page
- Dashboard
- Shipments Page
- Suppliers Page
- Inventory Page
- Alerts Page
- Analytics Page

---

## 4.2 Spring Boot Backend

Responsibilities

- REST API provider
- Business logic processing
- Authentication and authorization
- Risk scoring engine
- Alert generation
- ML service integration
- Database management

Key Modules

- Authentication Service
- Shipment Service
- Supplier Service
- Inventory Service
- Risk Prediction Service
- Alerts Service
- Analytics Service

---

## 4.3 Python ML Microservice

Purpose

Provide prediction capabilities.

Functions

- Shipment delay prediction
- Supplier risk classification
- Inventory shortage prediction

API Endpoints

POST /predict-delay  
POST /predict-supplier-risk  
POST /predict-inventory-risk

Input

Shipment features

Output

Prediction result

Example:

{
  "delay_probability": 0.82,
  "predicted_delay_hours": 5
}

---

## 4.4 PostgreSQL Database

Stores:

- user accounts
- shipments
- suppliers
- inventory
- predictions
- alerts
- analytics data

---

# 5. Data Flow

Step 1

User interacts with React dashboard.

Step 2

Frontend sends request to Spring Boot REST API.

Step 3

Spring Boot performs:

- validation
- business logic

Step 4

For predictions:

Spring Boot calls Python ML microservice.

Step 5

ML service returns prediction results.

Step 6

Spring Boot calculates risk score and stores result.

Step 7

Frontend displays predictions and alerts.

---

# 6. Risk Prediction Workflow

Shipment created →  
Shipment features extracted →  
Send features to ML API →  
ML predicts delay probability →  
Spring Boot calculates risk score →  
Alert generated if threshold exceeded →  
Dashboard updated.

---

# 7. Real-Time Alerts Architecture (Future Enhancement)

Future implementation may include WebSocket communication.

Architecture

React UI ←→ WebSocket Server ←→ Spring Boot

Benefits

- instant alert notifications
- live dashboard updates
- improved user awareness

---

# 8. Container Architecture (Docker)

Docker containers used:

1. react-frontend
2. springboot-backend
3. python-ml-service
4. postgres-database

Docker Compose orchestrates communication.

Ports

React → 3000  
Spring Boot → 8080  
Python ML → 5000  
PostgreSQL → 5432

---

# 9. Security Architecture

Authentication

JWT Token-based authentication

Authorization

Role-Based Access Control

Roles

Admin  
Operations Manager  
Supply Chain Analyst  
Logistics Manager

Security Features

Password hashing (BCrypt)

Token-based session management

Secure REST endpoints

---

# 10. Scalability Considerations

Microservice architecture allows:

- independent scaling of ML services
- separate database scaling
- stateless API scaling

Future scalability options

- Kubernetes
- Kafka event streaming
- distributed caching