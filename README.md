# Spring Boot example app for Spring Boot and Angular2 Tutorial
 
[![Build Status](https://travis-ci.org/springboot-angular2-tutorial/boot-app.svg?branch=master)](https://travis-ci.org/springboot-angular2-tutorial/boot-app)
[![Coverage Status](https://coveralls.io/repos/github/springboot-angular2-tutorial/boot-app/badge.svg?branch=master)](https://coveralls.io/github/springboot-angular2-tutorial/boot-app?branch=master)

This repository is an example application for Spring Boot and Angular2 tutorial.

[Demo](https://micropost.hana053.com/)

* JWT
* [Querydsl](http://www.querydsl.com/)
* [Spock](http://spockframework.org/)

## Getting Started

Run Spring Boot.

```
./gradlew bootRun
```

Serve frontend app.

```
git clone https://github.com/springboot-angular2-tutorial/angular2-app.git
# Follow the README
```

Testing.

```
./gradlew test
```

API documentation.

```
./gradlew bootRun
open http://localhost:8080/swagger-ui.html
```

## Frequently asked questions

* Q) Build becomes an error on IntelliJ IDEA with error message "QUser, QRelationship and etc can't be found".
* A) You must configure setting for Annotation Processors.
  1. Go to Preferences -> Build, Execution, Deployment -> Annotation Processors
  2. Check Enable annotation processing checkbox
  3. In "Store generated sources relative to:" select Module content root.
  4. Finally, Build -> Build Project
  

## Docker Support

Dev

```bash
./gradlew clean build -x test
docker build -t YOUR_IMAGE_NAME .
docker run -p 8080:8080 YOUR_IMAGE_NAME
```

Prod

```bash
./gradlew clean build -x test
docker build --build-arg JASYPT_ENCRYPTOR_PASSWORD=secret -t YOUR_IMAGE_NAME .
docker run -p 8080:8080 \
  -e "SPRING_PROFILES_ACTIVE=prod" \
  -e "MYSQL_ENDPOINT=dbhost:3306" \
  -e "NEW_RELIC_LICENSE_KEY=newrelic licence key" \
  YOUR_IMAGE_NAME
```

## Tutorial

Under construction...

## Related Projects

* [Angular2 app](https://github.com/springboot-angular2-tutorial/angular2-app)
* [Android app](https://github.com/springboot-angular2-tutorial/android-app)
* [Infrastructure by Terraform](https://github.com/springboot-angular2-tutorial/micropost-formation)
* [Lambda functions by Serverless](https://github.com/springboot-angular2-tutorial/micropost-functions)

## Credits

* [Rails tutorial](https://github.com/railstutorial/sample_app_rails_4)

## License

[MIT](/LICENSE)
