# Smart Supply Chain Risk Prediction System (SSRP)

An AI-powered logistics intelligence platform that predicts shipment delays and calculates supply chain risk using machine learning.

## Features

- Shipment delay prediction using ML
- Risk score engine
- Supplier analytics
- Real-time alerts
- Logistics dashboard
- Docker-based microservice architecture

## Tech Stack

Frontend:
- React
- Vite
- Axios

Backend:
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate

ML Service:
- Python
- Scikit-learn
- XGBoost

Database:
- PostgreSQL

Infrastructure:
- Docker
- Docker Compose

## Architecture

The system consists of four services:

- React frontend
- Spring Boot backend API
- Python ML prediction service
- PostgreSQL database

All services are containerized and orchestrated using Docker Compose.

## Screenshots

### Login(password:password)

<img width="1918" height="866" alt="image" src="https://github.com/user-attachments/assets/0199efc9-7515-4aa7-97c1-0d42f046db04" />


### Dashboard

<img width="1918" height="868" alt="image" src="https://github.com/user-attachments/assets/39bcd4e3-edc8-4922-aebb-73468d6a6d1c" />


### Shipments

<img width="1919" height="830" alt="image" src="https://github.com/user-attachments/assets/27aaab17-9e1f-4958-897c-9dc82193d810" />

### Prediction

<img width="1919" height="963" alt="image" src="https://github.com/user-attachments/assets/ff48292e-de52-4707-a2c7-7630621275e0" />


### Supplier

<img width="1919" height="843" alt="image" src="https://github.com/user-attachments/assets/32857771-6ad6-470c-aa91-ee379e9243e4" />


### Alerts

<img width="1906" height="690" alt="image" src="https://github.com/user-attachments/assets/59026d57-bcee-48b3-9d12-ef7c15e6677c" />


### Analytics

<img width="1918" height="866" alt="image" src="https://github.com/user-attachments/assets/b139f8e7-ff0d-49c5-b168-6387367b231f" />


## Local Deployment

Run the application locally using Docker:

```
docker compose up --build
```

Access the system at:

```
http://localhost:3000
```

## Future Enhancements

- Cloud deployment
- Real-time WebSocket alerts
- Route optimization
- Advanced AI models
