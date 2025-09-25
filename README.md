# Tinder. Internet Applications Lab 25-26

### About de repository

This repository has two branches, both implementing the same application but:
- **tinderNOSecurity branch** is the plain application with no security implemented
- **tinderSecurity branch** security is implemented so that users are authenticated and authorized

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

## OPENAPI and Swagger
We added the following dependency to add the OPENAPI documentation to our project
```xhtml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.7.0</version>
</dependency>
```
OpenAPI is a specification of how to document a REST API. The documentation is machine-readable so that it is possible to automatically generate the
boiler code of a REST controller. Swagger is a set of tools to generate code from a specification or to generate a specification from code. 
In our case you can access:
- http://localhost:8080/swagger-ui/index.html to visualize and interact with the API
- http://localhost:8080/v3/api-docs to visualize the OPENAPI documentation of the API

For more information you can go to [[Learn OPNAPI]](#6) or directly to the root [[OPENAPI]](#7)

## Security implementation with Spring Security and Json Web Tokens (JWT)
### Credits
This implementation is based on [[Dan Vega's tutorial]](#8). He introduces security with JWT and using the Spring Security OAUTH2 Resource
Server instead of programming a Security Filter for the JWT from scratch as many tutorials (incorrectly) do.

However, this example has two main differences with the Dan Vega's tutorial:
* Here, we store the users in the **database** instead of using an *InMemoryUserDetailsManager* which stores users in memory.
* We use the **HS512** algorithm to sign the JWT token instead of the *RS256* algorithm. The HS512 algorithm uses a single secret key to sign the token,
  while the RS256 algorithm uses a pair of public and private keys. The RS256 algorithm is more secure because the private key is never shared, but it is more complex to implement.
  The HS512 algorithm is simpler and is enough for this example.

### The Implementation
This example introduces the basic concepts of Spring Security. It uses JSON Web Tokens (JWT), and you can [[JWT]](#9). Since REST APIs are stateless, we need to use
a mechanism to authenticate and authorize the user in each request. JWT is an encrypted token containing the user's information
(name and permissions). We must send this token to the server in the header of each HTTP request. So, the server will
validate the token and allow the user to access the resource if the token is valid and the user has the necessary permissions.
The server must sign the token for it to be valid. The server can then check the signature to see if the token is valid.
In this example, we use the HS256 algorithm to sign the token with a single secret key (the signature). There are other algorithms
that use public and private keys to sign the token.

We can divide the security process into two main parts: authentication and authorization.
* **Authentication** is the process of verifying the identity of a user. That is, to check if the user is who he says he is,
  and the process usually uses a username and password. In our example, when the user logs in, the server will check the username
  and password and, if they are correct, will return a JWT token with his name and credentials. We also need to
  store the registered users' passwords encrypted in the database. The encryption is only one way, so we cannot decrypt the password.
  In order to compare the password that the user sends with the password stored in the database, we need to encrypt the password sent
  and compare it against the encrypted one stored in the database.
* **Authorization** is the process of verifying what the user has access to. That is, to check if the user has the necessary
  permissions to access the requested resources. When the user makes a request to the server, the client will send the JWT token
  in the header of the request. The server will check the token; if it is valid and the user has permission, it will allow the user to access the resource.
  Spring Security will also add the user's information to the SecurityContext, so we can access it in the Rest controller.

In order to use Spring Security, we need to add the following dependency to the `pom.xml` file:
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
```
Note that we use a part of the OAuth2 protocol that uses JWT tokens instead of the `spring-boot-starter-security` dependency.
Once we have added the dependency, we need to configure the security chain. The configuration is placed in the security package. The authentication is defined
in the authentication package, and contains the following classes:
* **'AuthenticationRequest'** is a DTO that represents the request that the client sends to the server when the user logs in. It contains
  the username and password.
* **'AuthenticationResponse'** is a DTO that represents the response that the *AuthenticationService* returns when the user logs in. It contains
  the JWT token created by the service.
* **'AuthenticationController'** is a REST controller that receives the login request, calls the AuthenticationService and returns the
  JWT token to the client.
* **'AuthenticationService'** is a service that receives the login request, checks the username and password, creates the JWT token,
  and returns it within an AuthenticationResponse object when authentication is correct. Otherwise, it throws an exception. This class uses the 'UserDetailsService' (defined in its file),
  JwtEncoder and an AuthenticationManager (defined in the SecurityConfigurationBeans file).
* **'JwtEncoder'** and **'JwtDecoder'** are two beans defined in the SecurityConfigurationBeans file used to encode and decode the JWT token. The AuthenticationService uses the encoder to create the token when the user logs in. At the same time, the AuthenticationFilter uses the decoder to check the token
  when the user requests the server and includes the token in to the HTTP headers. Recall that the token contains the user's name and permissions,
  and so, if the token is valid, the system extracts the user information from it without going to the database.
* **'AuthenticationManager'** is an interface that defines the method *authenticate*, which receives the login and password and returns the authenticated user
  with his permissions. To do so, it needs to find the user from the database (using the UserLabDetailsService) and encrypt the password (using the
  PasswordEncoder defined in the SecurityConfigurationBeans) to compare it against the one in the database.
* **'UserLabDetailsService'** implements the UserDetailsService interface and is in charge of getting the user from the database and returning a UserDetails object.
* **'UserLabDetails'** implements the UserDetails interface and represents the user for the security module in Spring. It contains the user's name, password,
  permissions, and other information such as whether the account is active.

The **authorization** is defined in the authorization package, and only contains the class **'SecurityConfigurationAuthorization'** where a bean of
type SecurityFilterChain is defined. As you can see in the next section, security in Spring contains a chain of filters that intercepts the requests
to the server and carries all the security checks before the request reaches the Rest controller.

This filter configures other security characteristics apart from the authorization rights for each endpoint. Namely, it defines:
* CORS (Cross-Origin Resource Sharing) configuration allows the server to receive requests from other domains. An actual application may limit the domains that can access the server to the ones that host the JavaScript code of the Web HTML pages.
* CSRF (Cross-Site Request Forgery) is an attack that tricks the user into executing unwanted actions on a web application in which they are
  authenticated and a session is active. This protection is disabled because REST APIs are stateless, so there is no session to protect.
* In the headers, we allow the client to use HTML frames when they all come from the exact origin. Allowing frames from different origins can be a
  security risk, making it easier to perform a CSRF attack.
* The session management is set to stateless because REST APIs are stateless. This means the server does not store the user's session, so
  it does not send a cookie to the client.
* The resource server is configured using the JWT decoder defined in the SecurityConfigurationBeans file. We are using a part of the OAuth2 protocol, and our application plays the role of the resource server. The Oauth2 protocol is a story
  for another day, but you can read about it [[OAUTH2]](#11).
* The HTTP basic is a security scheme that allows the client to authenticate with the server using a username and password. In our case,
  we are not using it.
* And finally, we define the authorization rights for each endpoint. In the **authorizeHttpRequests**, we add a RequestMatcher for each endpoint or
  set of endpoints that we want to protect. The matcher receives a list of path templates (endpoints) that will be compared
  with the path of the request and the permissions the user needs to access the resource. When the request path matches one of the templates,
  the server will check if the user has the necessary permissions. It is important to note that when a request matches more than one RequestMatcher,
  the server will use the first added to the SecurityFilterChain. So, the order of the RequestMatchers is important.

Path templates are strings that can contain wildcards. The wildcards are '*' and '**'. The '*' wildcard matches any character except the path separator
while the '**' wildcard matches any character including the path separator. The path separator is the character '/' that separates the directories in the path.
Request template examples:
* requestMatchers("/hello") will match the path "/hello"
* requestMatchers("/hello/*") will match the path "/hello/anything" but not "/hello/anything/anythingElse"
* requestMatchers("/hello/**") will match the path "/hello/anything/anythingElse/.../anything"
* requestMatchers("/hello/*/bye") will match the path "/hello/anything/bye" but not "/hello/anything/anything/bye"

The permissions are strings that represent the user's roles. We will give permissions depending on the roles. For example:
* requestMatchers("/hello").permitAll() will allow all users to access the resource '/hello'
* requestMatchers("/hello").authenticated() will allow only authenticated users to access the resource '/hello' independently of their roles
* requestMatchers("/hello").access(hasScope("USER") will allow only users with the role "USER" to access the resource '/hello'
* requestMatchers("/hello").access(hasAnyScope("USER", "ADMIN") will allow only users with the roles "USER" or "ADMIN" to access the resource '/hello'
  Note that a user may have more than one role.

The importance of the order of the RequestMatchers. If we have the following code,
```java
.authorizeHttpRequests(auth -> {
    auth.requestMatchers("/**").permitAll();
    auth.requestMatchers("/helloUser").access(hasScope("USER"));
})
```
All requests will be allowed because the first RequestMatcher matches all paths.

### Testing the Security
Integration --> authentication
Unit Security --> authorization
### Spring Security Architecture
Security is a cross-cutting concern, and it uses a filter that intercepts the requests to the server. You can see the
official documentation [[Security docs]](#10)

## References
- <a id="1">[Vlad Persist]</a>  https://vladmihalcea.com/jpa-persist-merge-hibernate-save-update-saveorupdate/
- <a id="2">[Hello Koding]</a> https://hellokoding.com/composite-primary-key-in-jpa-and-hibernate/
- <a id="3">[RESTful]</a> https://learn.microsoft.com/en-us/azure/architecture/best-practices/api-design
- <a id="4">[Vlad Composite]</a> https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
- <a id="5">[Vlad @oneToMany]</a> https://vladmihalcea.com/the-best-way-to-map-a-composite-primary-key-with-jpa-and-hibernate/
- <a id="6">[Learn OPNAPI]</a> https://learn.openapis.org/introduction.html 
- <a id="7">[OPENAPI]</a> https://www.openapis.org/
- <a id="8">[Dan Vega's tutorial]</a> https://www.danvega.dev/blog/spring-security-jwt
- <a id="9">[JWT]</a> https://jwt.io/introduction/
- <a id="10">[Security docs]</a> https://docs.spring.io/spring-security/reference/servlet/architecture.html
- <a id="11">[OAUTH2]</a> (https://auth0.com/intro-to-iam/what-is-oauth-2)