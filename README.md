# Teamplanner

## Vision

The purpose of this stateless REST API built with JPA was to reduce the workload of the scheduling coordinator.

A small scheduling system has been developed with the goal of allowing employees to automatically release their shifts, while other employees can respond indicating whether they are able to take the shift or not. 

In addition, employees can request vacation for a specified start and end date. This enables the manager to easily filter employees based on shared responsibilities, making it simpler to plan vacations, as it is often undesirable for people with the same responsibilities to be away at the same time.

---

## Links

Portfolio website:  
[RONEU.DK](https://roneu.dk/portfolio)

Project overview video (max 5 min):  
[PREVIEW](https://www.youtube.com/)

Deployed application:  
[TeamPlanner API routes](https://teamplanner.roneu.dk/routes)

Source code repository:  
[GitHub](https://github.com/Rovelt123/Semesterprojekt-3.Sem)

---

# Architecture

## System Overview

### This project is built as a layered backend architecture using:
- Controller layer (REST endpoints)
- Service layer (business logic)
- DAO layer (data access)
- DTO layer (data transfer objects)
- Security layer (authentication and authorization with JWT)

### Technologies used:
- Java
- Javalin
- JPA / Hibernate
- PostgreSQL
- Maven
- JWT authentication


---

## Diagrams

### ERD
![ERD](https://raw.githubusercontent.com/Rovelt123/Semesterprojekt-3.Sem/refs/heads/master/src/Diagrammer/ERD.png?token=GHSAT0AAAAAADLIX47IGXG5ORCGIXPHBUMK2OOSPJA)

### Classdiagram
![CLASS](https://raw.githubusercontent.com/Rovelt123/Semesterprojekt-3.Sem/refs/heads/master/src/Diagrammer/Klassediagram_final.png?token=GHSAT0AAAAAADLIX47ITYN6RBD5DUI3MGYC2OOSQGQ)


---

## Key Design Decisions

### Authentication

Authentication in the system is implemented using JSON Web Tokens (JWT).  
When a user successfully logs in, the server generates a signed JWT containing essential user information (such as user ID and role). This token is returned to the client and must be included in the `Authorization` header as a Bearer token for all subsequent API requests.

The server validates the token on each request by verifying its signature and expiration time. If the token is missing, invalid, or expired, the request is rejected.

This stateless authentication approach eliminates the need for server-side session storage and improves scalability, as the API does not need to maintain session state between requests. Additionally, role-based information inside the token can be used to control access to specific endpoints, improving overall security and access control.

### DTO

DTOs act as a controlled contract between the backend and frontend, defining exactly which fields are allowed in requests and responses. This also improves maintainability, since changes in the internal database schema do not necessarily affect the external API structure.

In addition, DTOs help reduce payload size and improve clarity by only including necessary fields rather than full entity graphs. In this project, mapping between entities and DTOs is handled directly in the controllers, which ensures a straightforward flow from request handling to response creation. While this approach is less ideal from a separation-of-concerns perspective, it was chosen due to time constraints.


### How's errors handled?

Error handling in the system is implemented through a centralized `TryCatchService`, which acts as a consistency layer for validation and parsing of input data. The purpose of this service is to standardize error handling across the application and ensure that invalid or incomplete data is detected early in the request lifecycle.

Instead of repeating try-catch logic across controllers and services, the `TryCatchService` encapsulates this functionality into reusable utility methods. This reduces code duplication while improving readability and maintainability.

The service handles parsing of primitive types (e.g. `int`, `double`, `boolean`), conversion of date and time formats, enum validation, list validation, null and empty checks, as well as request body deserialization.

All errors are consistently converted into a unified `ApiException`, which includes appropriate HTTP status codes (e.g. 400 Bad Request or 404 Not Found). This ensures a consistent error response structure across the API, making it easier for clients to handle failures in a predictable way.

This approach results in a more robust and defensive backend architecture, where errors are handled early and consistently.

## Important Entities

### User

Represents a registered user in the system

**Fields:**
- id
- firstname
- lastname
- username
- password
- roles
- responsibilities
- holidays
- shifts
- announcements
- responses


### Shift

Represents a shift for a user

**Fields:**
- id
- title
- owner
- date
- start time
- end time

### ShiftRequest

Represents a request of getting rid of a shift

**Fields:**
- id
- status
- requester (Owner of the shift)
- shift
- responses (A list of responses of all users)


***All of the above entities form a central part of the system’s domain model, as most of the application’s functionality is built around them. They represent the core business objects in the system and therefore play a crucial role in both data flow and application logic. For this reason, they are important to highlight in the project’s architecture.***

# API Documentation
### User

**LOGIN**

***This os for the user login, for the JWT to generate a token***

````
POST {{url}}/users/auth/login
Content-Type: application/json

{
    "username": "<USERNAME>",
    "password": "<PASSWORD>"
}

> {%
    client.global.set("token", response.body.data.token);
    console.log("JWT Token:", client.global.get("token"));
%}
````

**Register**

***This is how to register as a user on the system***
````
POST {{url}}/users/auth/register
Content-Type: application/json

{
  "first_name": "<FIRSTNAME>",
  "last_name": "<LASTNAME>",
  "username": "<USERNAME>",
  "password": "<PASSWORD>",
  "repeat_password": "<REPEAT>"
}
````

***Get all (Symmetrical for the whole API)***
- Could use /shift_requests, /shifts, /responsibilities to get all


````
GET {{url}}/users
Authorization: Bearer {{token}}
````

***Get by ID (Symmetrical for the whole API)***
- Same as get all -> /shift_request, /shift, /responsibility to get by ID


````
GET {{url}}/user/{id}
Authorization: Bearer {{token}}
````

# User Stories
 
- Being added soon!

# Development notes

- Being added soon!