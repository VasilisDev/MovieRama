# MovieRama
## Local development setup

### Prerequisites

- JDK 1.8
- MySQL 5.7+
- Maven 3+
- Node.js

### Database setup

- Create database `movierama`
- Initialize database with scripts in `setup` folder

### Replace dev properties file

- In `src/main/resources/application.properties` override the settings.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/movierama?useSSL=false
spring.datasource.username=<your username>
spring.datasource.password=<your password>
```

## Commands
In the parent folder follow the below commands:

- `mvn clean` to clean all previously build target sources
- `mvn test` to run unit and api integration tests of the back-end
- `mvn install` to build both the front-end and the back-end
- `java -jar target/movierama-0.0.1-SNAPSHOT` to start the packaged application

## Run application

After you run the jar, open your favourite browser and hit http://localhost:8080. The application is up and running.  
