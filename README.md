# Tinder. Internet Applications Lab 25-26

## Domain Overview

TinderLab2526 is a Spring Boot application that simulates the core features of a dating platform. The main focus is on user profiles,
each with personal attributes and preferences. Users can:

- Create and manage their profiles
- Like other profiles
- Discover compatible candidates based on mutual preferences

Profiles include fields such as nickname, email, gender, attraction, and passion. The system enforces compatibility for likes based on
attraction and passion attributes.

Although this is a simplified version of a dating app, all the core logic is implemented in the domain layer, following Domain-Driven Design (DDD) principles.
In line with DDD terminology, the *aggregate root* is the **Profile** entity, which contains all the business logic. The Profile entity maintains a list of *Likes*
that represent the profiles a user has liked. **Likes** are directional, meaning that if profile A likes profile B, it does not necessarily mean that profile B likes profile A.
When both profiles like each other, a *match* is created, and both profiles' likes are marked as matched. Gender, Attraction, and Passion are implemented as *value objects* using
enums.

## Architecture
The application follows a layered architecture with clear separation of concerns:
- **Persistence Layer**: Manages data storage and retrieval using Spring Data JPA
- **Domain Layer**: Contains the core business logic and entities
- **Application Layer**: Defines use cases and orchestrates domain entities fetching and saving data from/to the database
- **Presentation Layer**: Exposes RESTful APIs for client interaction

## Implementation Details
This application is built with Spring Boot and Spring Data JPA, using Maven for dependency management. The modular structure follows Domain-Driven
Design principles and ensures a clear separation of concerns. The use of the H2 in-memory database enables rapid prototyping and
simplifies testing, allowing developers to run and validate the application without external setup.

### Domain layer
The **Profile** aggregate in the domain layer contains all the business logic. It throws exceptions when a business rule is violated.
In this application, there is only one rule: if a user tries to like a profile that is not compatible with their attraction and passion,
an **IsNotCompatibleException** is thrown.

### Application layer
The application layer, **TinderService**, retrieves domain objects from the persistence layer, calls domain methods to execute the business logic,
and saves them back to the database. Domain objects never leave the application layer. It always returns DTOs to the
presentation layer (**ProfileInformation** and **LikeInformation**). When the presentation layer needs to return DTO objects, it uses mapper classes
(**ProfileMapper** and **LikeMapper**) to map domain objects into DTOs.

This layer throws exceptions related to application logic, primarily when accessing the database, such as **ProfileNotFoundException** when a profile
is not found in the database.

### Presentation layer
The presentation layer is implemented with a REST controller (**TinderRestController**) that exposes the application layer functionality via HTTP endpoints.

When new objects are created, Profiles in our case, the application layer gets a **ProfileCommand** object from the presentation layer. The
ProfileCommand is a simple DTO object that contains all the information needed to create a new Profile. It also contains validation annotations to ensure
that the data is valid before creating the Profile. When data is not valid, a **MethodArgumentNotValidException** or **ConstraintViolationException** is thrown by Spring Boot
before reaching the application layer.

This layer also handles exceptions thrown by the other layers and returns appropriate HTTP status codes and messages to the client. It uses global exception handling with **@ControllerAdvice**
and **@ExceptionHandler** annotations. It returns error messages in a **ProblemDetail** object, which is a standard way (RFC 9457) to represent errors in REST APIs.
Note also that the API adheres to RESTful principles, utilizing appropriate HTTP methods (GET, POST) and status codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found).
See [[RESTful]](#3) for a good REST API design. For example, when a new profile is created with a POST, the API returns a 201 Created status code with the
location of the new resource in the Location header and the body is empty.

### Persistence layer
This layer is implemented with Spring Data JPA. It may be the most novel part of the application if you have previously used *JDBC*. JPA (Java Persistence API)
is an ORM (Object Relational Mapping) specification, and one of its most common implementations is Hibernate. Spring Data JPA is an additional layer
on top of JPA with the Hibernate implementation.

Note that all the domain entities are annotated to specify how the classes and their objects should be mapped to relational tables and their rows.
Let's comment on some points of the domain annotations:
- All classes need an attribute annotated as an identifier because its objects will be a row in a relational database table
- The Like entity uses a different table name because *"like"* is a reserved SQL word
- A Profile has a list of Likes that is mapped with a @oneToMany annotation. It is more efficient in JPA to represent this kind of association with
  a bidirectional mapping [[Vlad @oneToMany]](#5). For this reason Like has an extra property (column in the database) pointing to the *origin* Profile.
- The identifier of Like is a composite ID formed by two foreign keys: one for the origin profile and one for the destination profile.
- The composed id of Like is implemented in the class LikePK. We chose to implement Like and LikePK as in [[Hello Koding]](#2), but we could have done
  it as in [[Vlad Composite]](#4)
- To query tables in the database, the application layer (**TinderService**) uses the **ProfileRepository** interface that extends CRUDRepository,
  which provides CRUD operations out of the box. Note also that the TinderService uses the **@Transactional** annotation to handle JPA sessions/transactions.
  Observe that when an object is modified within a transaction, JPA automatically updates the database. See [[Vlad Persist]](#1).

## Testing
We have three suites of tests:
1. **Domain Tests**: test all the domain logic in isolation from the rest of the layers. Note that there is a **ProfilesMotherTest** that is
   a sort of Profile factory for testing the logic.
2. **Service Tests**: test the service layer of the application and its integration with the persistence layer. So, we are testing
   that the service calls the domain layer correctly and also deals correctly with the persistence layer. By *deals correctly*, we
   mean that it retrieves the correct domain objects and updates the changes. Observe also that with these tests, we are indirectly
   testing the persistence layer.
3. **Integration Tests** test the application end to end. It focuses on testing the REST API entry points, to ensure that the results are correctly returned,
   that the rest controller calls the application layer correctly, and finally that it handles correctly all the errors. Both domain exceptions
   and input data validation exceptions.

## References
- <a id="1">[Vlad Persist]</a>  https://vladmihalcea.com/jpa-persist-merge-hibernate-save-update-saveorupdate/
- <a id="2">[Hello Koding]</a> https://hellokoding.com/composite-primary-key-in-jpa-and-hibernate/
- <a id="3">[RESTful]</a> https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design
- <a id="4">[Vlad Composite]</a> https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
- <a id="5">[Vlad @oneToMany]</a> https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/