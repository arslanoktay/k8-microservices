# Healthcare Microservices Platform

> Microservices-based sample platform demonstrating synchronous REST/gRPC, asynchronous event streaming with Kafka + Protobuf, centralized gateway with JWT validation, and typical cross-cutting concerns (validation, persistence, OpenAPI).


## 📖 Subjecs

*   [🔍 Project Overview](#-project-overview)
*   [🧩 Architecture & Communication Flow](#-architecture--communication-flow)
*   [🧱 Services & Key Tech (per service)](#-services--key-tech-per-service)
    *   [API Gateway](#api-gateway)
    *   [Auth Service](#auth-service)
    *   [Patient Service](#patient-service)
    *   [Billing Service](#billing-service)
    *   [Analytics Service](#analytics-service)
*   [🔐 Security (JWT + Gateway)](#-security-jwt--gateway)
*   [🛠 API Examples](#-api-examples)
*   [🧪 Development & Local Run (suggested)](#-development--local-run-suggested)
    *   [Prerequisites](#prerequisites)
    *   [Quick start with Docker Compose](#quick-start-with-docker-compose)
    *   [Run locally](#run-locally)
*   [📈 Observability & Monitoring (recommended)](#-observability--monitoring-recommended)
*   [✅ Testing](#-testing)
*   [🤝 Katkıda Bulunma](#-katkıda-bulunma)
*   [📄 Lisans](#-lisans)
*   [✉️ İletişim](#-i̇letişim)

---

## 🔍 Project Overview

This project is a small-scale healthcare microservices example. It showcases common patterns used in enterprise microservice systems:

-   **Auth Service**: issues and validates JWT tokens for clients.
-   **API Gateway**: central entry point that validates JWTs and routes requests to downstream services.
-   **Patient Service**: canonical CRUD REST API for patient records (Spring Boot + JPA).
-   **Billing Service**: exposes gRPC endpoints for billing-related operations.
-   **Analytics Service**: Kafka consumer that processes domain events (serialized with Protobuf).
-   Additional services (User/Order/etc.) can be plugged in similarly.

The system demonstrates:
-   sync communication via REST and gRPC,
-   async event-driven communication via Kafka,
-   security via JWT validated at the gateway,
-   service-specific persistence (Postgres/H2/JPA),
-   OpenAPI/Swagger UI for REST endpoints,
-   containerized setup (Docker / Docker Compose) for local development.


---

## 🧩 Architecture & Communication Flow

<img width="630" height="352" alt="resim" src="https://github.com/user-attachments/assets/4f4c6265-76e2-483b-9ce8-a25e65891052" />

1.  **Client ➜ API Gateway**  
    Clients authenticate to Auth Service (via `/login`) and receive a JWT. The client sends the JWT to the **API Gateway** on each request (`Authorization: Bearer <token>`). The Gateway validates the token and routes requests to relevant services.

2.  **Gateway ➜ Services (REST / gRPC)**  
    -   REST: Gateway forwards HTTP requests to services such as Patient Service.  
    -   gRPC: Services may call each other via gRPC (e.g., Billing Service exposes gRPC APIs used by other services or by internal processes).

3.  **Event Bus (Kafka + Protobuf)**  
    Domain events (e.g. patient.created / patient.updated) are published to Kafka topics. Analytics Service subscribes to those topics, deserializes Protobuf messages and processes them.

4.  **Persistence**  
    Each stateful service manages its own data (PostgreSQL or H2 for tests). Migrations can be handled with Flyway (useful to add if required).

---

## 🧱 Services & Key Tech (per service)

### API Gateway
-   **Purpose:** central entry point, JWT validation, routing, rate-limiting (optional), can host resiliency filters.
-   **Tech:** Spring Cloud Gateway (`spring-cloud-dependencies` in `dependencyManagement`), Spring Boot.
-   **Responsibility:** validate JWT with Auth Service (or introspect token), forward requests to internal services.

### Auth Service
-   **Purpose:** authenticate users, issue JWT tokens, token validation endpoint.
-   **Tech:** Spring Boot, Spring Security, JJWT (`io.jsonwebtoken`), PostgreSQL (JPA), `springdoc-openapi`.
-   **Exposed endpoints:**
    -   `POST /login` → returns JWT on successful credentials
    -   `GET /validate` → validates token (used by Gateway or debugging)

### Patient Service
-   **Purpose:** REST CRUD API managing patients (public/internal API via Gateway).
-   **Tech:** Spring Boot, Spring Data JPA, PostgreSQL (H2 for tests), OpenAPI (`springdoc`), gRPC stubs present (if interop needed).
-   **Endpoints (sample):**
    -   `GET /patients` → list patients
    -   `POST /patients` → create patient (validations using `@Validated` and groups)
    -   `PUT /patients/{id}` → update patient
    -   `DELETE /patients/{id}` → delete patient

### Billing Service
-   **Purpose:** billing-related operations exposed via gRPC.
-   **Tech:** gRPC (`grpc-netty-shaded`, `grpc-protobuf`, `grpc-stub`), protobuf definitions, Spring Boot gRPC starter.
-   **gRPC methods (example):**
    -   `createBillingAccount(BillingRequest) → BillingResponse`
    -   (Implementations use `StreamObserver` as in your `BillingGrpcService`)

### Analytics Service
-   **Purpose:** consumes Kafka events (topic: `patient`) and performs analytics / logging / downstream processing.
-   **Tech:** Spring Boot, Spring Kafka, Protobuf (`protobuf-java`), `spring-kafka-test` for testing.
-   **Behavior:** `@KafkaListener(topics = "patient", groupId = "analytics-service")` deserializes Protobuf bytes into `PatientEvent` and processes.

---

## 🔐 Security (JWT + Gateway)
-   **Authentication flow:**
    1.  Client performs `POST /login` (Auth Service) → receives JWT.
    2.  Client includes `Authorization: Bearer <token>` on requests to Gateway.
    3.  Gateway validates token (either by introspecting with Auth Service `/validate` or verifying signature via public key / shared secret).
    4.  If valid, Gateway forwards request to downstream service (optionally adding `X-User-Id` header).
-   **Notes:**
    -   Auth Service uses JJWT to sign tokens.
    -   Gateway performs token validation to avoid sending unauthenticated requests to internal services.
    -   Internal services can trust the Gateway and perform lightweight checks (or validate tokens themselves for defense-in-depth).

---


