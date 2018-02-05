# Java challenge

This is Spring boot application which does few accounts operations.
- create new account.
- get the account details.
- transfer money from one account to account.

### Build

This project use gradle for build and installation.
``` 
gradle clean build
```

To check the code coverage use command with unit tests
```
gradle clean build jacocoTestReport
```
 
### Deploy
You can deploy the project using below command
```
java -jar build/libs/Java-Challenge-0.0.1-SNAPSHOT.jar
```
***Note:*** You need to have Java 1.8 already installed on the machine.

### Production deployment

- The application should have different profiles to work on. As current application supports production profile and 
    development profile
- Spring boot by default supports profiles. For production level deployment you can run the project using below command
``` 
java -Dspring.profiles.active=production -jar build/libs/Java-Challenge-0.0.1-SNAPSHOT.jar
```
- The logging level for production level should be ERROR, so only errors will be logged.
- The logs should be appended in a file. Currently logs are getting appended in java-dev-challenge.log file.

### Future enhancement needed for production support
- To add error tracking by generating unique error id, adding it as part of error response and logging in application 
    error logs for any error occurred in the REST. So that the error can be tracked as part of logs in production support.
- Adding service discovery (can use Netflix Eureka) in the application so that APIs can be discovered.
- Adding load balancer (can use Netflix Ribbon) in the application for load balancing, fault tolerance and caching.
- Adding security and monitoring (can use Netflix Zuul) in the application for authentication and monitoring.

### Future enhancement needed for application code quality 
- To add the test cases for the repository layers.
- To segregate the domain model which is same for web and repository level.


