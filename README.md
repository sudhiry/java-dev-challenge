# Java challenge

This is Spring boot application which does few accounts operations.
- create new account.
- get the account details.
- transfer money from one account to account.

### Build
This project use gradle for build and installation.
``` gradle clean build```
To check the code coverage use command with unit tests
``` gradle clean build jacocoTestReport```
 
### Deploy
You can deploy the project using below command
```
java -jar build/libs/Java-Challenge-0.0.1-SNAPSHOT.jar
```
***Note:*** You need to have Java 1.8 already installed on the machine.

### Production deployment

- The application should have different Profiles to work on. As current application supports production profile and development profile
- For production level deployment you can run the project using below command
``` 
java -Dspring.profiles.active=production -jar build/libs/Java-Challenge-0.0.1-SNAPSHOT.jar
```
- Spring boot by default supports profiles, so for production deployment you can provide active profile as production.

