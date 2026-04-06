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
![ERD](/src/Diagrammer/ERD.png)

### Classdiagram
![CLASS](/src/Diagrammer/Klassediagram_final.png)


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



---


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
## Authentication & Authorization
### Authentication

- As a guest, I want to register a user
- As a user, I want to log in

### Authorization

- As a system, I want to restrict access based on roles, so that only authorized users can perform certain actions
- As a boss, I want to assign roles to users, so that I can control access
- As a boss, I want to remove roles from users, so that I can manage access
- As a user, I want to be denied access if I lack permissions, so that security is enforced

---

## User Management
- As a user, I want to view all users
- As a user, I want to view a specific user by ID
- As a user, I want to find a user by username
- As a user, I want to filter users by role
### Profile Management
- As a user, I want to update my username
- As a user, I want to update my password, so that I can keep my account secure
- As a user, I want to update my personal information
- As a user, I want to delete my user
### Admin (boss)
- As a boss, I want to update any user, so that I can manage user data
- As a boss, I want to force delete a user
- As a boss, I want to filter users by responsibility, so that I can manage teams
- As a boss, I want to assign responsibilities to users, so that I can define their roles
- As a boss, I want to remove responsibilities from users

---

## Shift Management
### Viewing Shifts
- As a user, I want to view all shifts
- As a user, I want to view a shift by ID
- As a user, I want to view shifts by date
- As a user, I want to view my own shifts, so that I know when I work

### Managing Shifts (boss)
- As a boss, I want to create a shift
- As a boss, I want to update a shift
- As a boss, I want to delete a shift
- As a boss, I want to schedule shifts for future months, so that I can plan ahead

---

## Shift Request System
- As a user, I want to create a shift request, so that I can give away a shift
- As a user, I want to view all shift requests, so that I can see available shifts
- As a user, I want to view a shift request by ID, so that I can see details
- As a user, I want to delete my shift request, so that I can cancel it
### Admin
- As a boss, I want to clean up old shift requests, so that the system stays organized
- As a boss, I want to be able to update any shift requests

---

## Response System
- As a user, I want to respond to a shift request, so that I can offer to take a shift
- As a user, I want to reject a shift, so that I can decline it
- As a user, I want to mark no response, so that I can reset my decision
- As a owner of shift request, I want to be able to see all responses to my shift request
### Admin
- As a boss I want to see all responses
- As a boss I want to fetch a response by ID
- As a boss I want to fetch all responses by a shift request ID
- As a boss I want to fetch all responses by User ID
- As a boss, I want to manage responses, so that I can ensure shifts are covered
- As a boss, I want to delete responses, so that I can remove invalid data

---

## Holiday Management
- As a user, I want to request holiday
- As a user, I want to update my holiday request, so that I can change my plans
- As a user, I want to view my holidays
### Admin
- As a boss, I want to view all holiday requests, so that I can schedule holidays
- As a boss, I want to approve holiday requests
- As a boss, I want to reject holiday requests
- As a boss, I want to filter holidays by responsibility, so that I can plan staffing

---

## Responsibility Management
- As a user, I want to view all responsibilities
- As a user, I want to view a responsibility by ID or name
### Admin
- As a boss, I want to create a responsibility, so that I can define roles
- As a boss, I want to update a responsibility, so that I can rename it
- As a boss, I want to delete a responsibility, so that I can remove unused roles

---

## Announcements
- As a user, I want to view announcements, so that I stay informed
- As a user, I want to view announcements by author
### Admin
- As a boss, I want to create announcements, so that I can inform employees
- As a boss, I want to update announcements, so that I can update information
- As a boss, I want to delete announcements, so that I can remove outdated messages

---

## System & Utility
- As a user, I want to view all API routes, so that I can understand available endpoints
- As a user, I want to check system health/version, so that I know the API is running and up to date!
