# TinderLab2526

## Domain Overview

TinderLab2526 is a Spring Boot application that simulates core features of a dating platform. The domain centers around user profiles, 
each with personal attributes and preferences. Users can:

- Create and manage their profiles
- Like other profiles
- Discover compatible candidates based on mutual preferences

Profiles include fields such as nickname, email, gender, attraction, and passion. The system enforces compatibility for likes based on
attraction and passion attributes.

Even it is a simplified version of a dating app, all the logic is implemented in the domain layer, following Domain-Driven Design (DDD) principles.
Following the DDD nomenclature, the *aggragate root* is the **Profile** entity, which contains all the business logic. The Profile entity has a list of *Likes* 
that represent the profiles that a user has liked. **Lkies** are directional, meaning that if profile A likes profile B, it does not imply that profile B likes profile A.
When both profiles like each other, we have a *match* and both profiles' likes are set to matched. Gender, Attraction and Passion are implemented as *value objects* with
an Enum.

## Architecture
The application follows a layered architecture with clear separation of concerns:
- **Persistence Layer**: Manages data storage and retrieval using Spring Data JPA
- **Domain Layer**: Contains the core business logic and entities
- **Application Layer**: Defines use cases and orchestrates domain entities fetching and saving data from/to the database
- **Presentation Layer**: Exposes RESTful APIs for client interaction

## Implementation Details
This application is built with Spring Boot and Spring Data JPA, using Maven for dependency management. The modular structure follows Domain-Driven 
Design principles, ensuring clear separation of concerns. The use of the H2 in-memory database enables rapid prototyping and 
simplifies testing, allowing developers to run and validate the application without external setup.

### Domain layer
The aggregate of the domain layer, **Profile**, has all the business logic. It is throws exceptios when a business rule is violated. 
In our application there is only one rule: when a user tries to like a profile that is not compatible with his/her attraction and passion, 
a **IsNotCompatibleException** is thrown.

### Application layer
The application layer, **TinderService**, gets domain objects from the persistence layer, calls the domain methods to execute the business logic, 
and saves the domain objects back to the database. Domain objects never leak outside the application layer. It always returns DTOs to the 
presentation layer (**ProfileInformation** and **LikeInformation**). When the presentation layer needs to return DTO objects it uses mapper classes 
(**ProfileMapper** and **LkeMapper**).

This layer throws exception related to the application logic, basically accessing the database, like **ProfileNotFoundException** when a profile 
is not found in the database.

### Presentation layer
When new objects are created, Profiles in our case, the application layer gets a **ProfileCommand** object from the presentation layer. The 
ProfileCommand is a simple DTO object that contains all the information needed to create a new Profile. It also contains validation annotations to ensure 
that the data is valid before creating the Profile. When data is not valid, a **MethodArgumentNotValidException** or **ConstraintViolationException** is thrown by Spring Boot
before reaching the application layer.

The presentation layer is implemented with a REST controller (**TinderRestController**) that exposes the application layer functionality via HTTP endpoints.
This layer also handles exceptions thrown by the other layers and returns appropriate HTTP status codes and messages to the client. It uses global exception handling with **@ControllerAdvice** 
and **@ExceptionHandler** annotations. It returns error messages in a **ProblemDetail** object, which is a standard way (RFC 9457) to represent errors in REST APIs.
Note also that the API follows the RESTful principles, using appropriate HTTP methods (GET, POST) and status codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found).
(https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design). For example, when a new profile is created with a POST, the API returns a 201 Created status code with the 
location of the new resource in the Location header and the body is empty.

### Persistence layer
It is implemented with Spring Data JPA. It may be the most novel part of the application since you used *JDBC* in the past.
The **ProfileRepository** interface extends CRUDRepository, which provides CRUD operations.

