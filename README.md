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
(**ProfileMapper** and **LkeMapper**) to map a domain object into a DTO.

This layer throws exceptions related to the application logic, basically accessing the database, like **ProfileNotFoundException** when a profile 
is not found in the database.

### Presentation layer
The presentation layer is implemented with a REST controller (**TinderRestController**) that exposes the application layer functionality via HTTP endpoints.

When new objects are created, Profiles in our case, the application layer gets a **ProfileCommand** object from the presentation layer. The 
ProfileCommand is a simple DTO object that contains all the information needed to create a new Profile. It also contains validation annotations to ensure 
that the data is valid before creating the Profile. When data is not valid, a **MethodArgumentNotValidException** or **ConstraintViolationException** is thrown by Spring Boot
before reaching the application layer.

This layer also handles exceptions thrown by the other layers and returns appropriate HTTP status codes and messages to the client. It uses global exception handling with **@ControllerAdvice** 
and **@ExceptionHandler** annotations. It returns error messages in a **ProblemDetail** object, which is a standard way (RFC 9457) to represent errors in REST APIs.
Note also that the API follows the RESTful principles, using appropriate HTTP methods (GET, POST) and status codes (200 OK, 201 Created, 400 Bad Request, 404 Not Found).
See [RESTful] for a good REST API design. For example, when a new profile is created with a POST, the API returns a 201 Created status code with the 
location of the new resource in the Location header and the body is empty.

### Persistence layer
It is implemented with Spring Data JPA. It may be the most novel part of the application since you used *JDBC* in the past. JPA (Java Persistence API)
is an ORM (Object Relational Mapping) specification, and one of its most common implementations is Hibernate. Spring Data JPA is an additional layer
on top of JPA with the Hibernate implementation.

Note that all the domain entities are annotated to specify how the classes and its objects should be mapped to relational tables and its rows.
Let comment some points of the domain annotations:
- All classes need an attribute annotated as an identifier because its objects will be a row in a relational database table
- Like changes the table name because *"like"* is a SQL reserved word
- A Profile has a list of Likes that is mapped with a @oneToMany annotations. It is more efficient in JPA to represent this kind of association with
a bidirectional mapping [Vlad @oneToMany]. For this reason Like has an extra property (column in the database) pointing to the *origin* Profile.
- The identifier of Like is a composed id formed by two foreign keys, one for the origin and the other for the destination Profiles.
- The composed id of Like is implemented in the class LikePK. We chose to implement Like and LikePK as in [Hello Koding], but we could have done
it as in [Vlad Composite]
- To query tables in the database, the application layer (**TinderService**) uses the **ProfileRepository** interface that extends CRUDRepository, 
which provides CRUD operations out of the box. Note also that the TinderService uses the **@Transactional** annotation to handle JPA sessions/transactions.
Observe that when an object is modified within a transaction, JPA updates automatically the database. See [Vlad Persist].


## References

- [Hello Koding](https://hellokoding.com/composite-primary-key-in-jpa-and-hibernate/)
- [RESTful](https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design)
- [Vlad Composite](https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/)
- [Vlad @oneToMany](https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/)
- [Vlad Persist](https://vladmihalcea.com/jpa-persist-merge-hibernate-save-update-saveorupdate/)