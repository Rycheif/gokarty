# Gokarty

**Gokarty** is a [Spring Boot](https://spring.io/guides/gs/spring-boot) application built
using [Maven](https://spring.io/guides/gs/maven/). You can build a jar file and run it from the command line (it should
work just as well with Java 17 or newer) or simply run it using your favourite IDE.

The app runs on [http://localhost:8080/](http://localhost:8080/).

## Database

**Gokarty** uses PostgreSQL. Default configuration requires for database to already run on a server. The files needed to
create the database are in the /db_schema_generation_script directory.
There is also possibility to populate the database with data. /db_schema_generation_script directory contains a file
data_generation.sql. It has functions that populate all tables in the database with data.
To configure database connection go to /resources/application.yaml.

## Mail

**Gokarty** in two cases sends emails (after making reservation and after registration). In its default configuration
the app is set up to work with MailDev. To change this go to /resources/application.yaml.

## Working with Gokarty in your IDE

The following items should be installed in your system:

- Java 17 or newer (full JDK, not a JRE).
- [git command line tool](https://help.github.com/articles/set-up-git)
- Your preferred IDE

After starting the app it runs on [http://localhost:8080](http://localhost:8080).

## API

Here are all endpoints available:

| Endpoint                         | Method | Authorization | Role                                 |
|----------------------------------|--------|---------------|--------------------------------------|
| /api/user/{userId}               | GET    | YES           | ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE |
| /api/user/{userId}               | GET    | YES           | ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE |
| /api/users                       | GET    | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/user/lock/{userId}          | GET    | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/user/checkEmailAvailability | GET    | NO            | -                                    |
| /api/user/updateUserInfo         | PATCH  | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/user/updateUsersRoles       | PATCH  | YES           | ROLE_ADMIN                           |
| /api/kart/{kartId}               | GET    | NO            | -                                    |
| /api/karts                       | GET    | NO            | -                                    |
| /api/kart/updateKartData         | PATCH  | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/newKart                     | POST   | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/register                    | POST   | NO            | -                                    |
| /api/activateAccount             | GET    | NO            | -                                    |
| /api/reservation                 | GET    | YES           | ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE |
| /api/reservations                | GET    | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/usersReservations           | GET    | YES           | ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE |
| /api/reservationsFromDate        | GET    | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/availableReservationTimes   | GET    | NO            | -                                    |
| /api/reservation                 | POST   | YES           | ROLE_USER, ROLE_ADMIN, ROLE_EMPLOYEE |
| /api/track/{trackId}             | GET    | NO            | -                                    |
| /api/tracks                      | GET    | NO            | -                                    |
| /api/track/updateTrackInfo       | PATCH  | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |
| /api/newTrack                    | POST   | YES           | ROLE_ADMIN, ROLE_EMPLOYEE            |

## Tests

Here is a list of class which have unit tests written for them:

- AppUserService
- EmailConfirmationTokenService
- EmailSender
- AppUserMapper
- EmailValidator
- KartMapper
- ReservationMapper
- StringFileLoader

## Technology stack

* Java 17
* Spring Boot 3.0
* PostgreSQL

