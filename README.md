# Spring Boot example app for Spring Boot and Angular2 Tutorial
 
[![Build Status](https://travis-ci.org/springboot-angular2-tutorial/boot-app.svg?branch=master)](https://travis-ci.org/springboot-angular2-tutorial/boot-app)
[![Coverage Status](https://coveralls.io/repos/github/springboot-angular2-tutorial/boot-app/badge.svg?branch=master)](https://coveralls.io/github/springboot-angular2-tutorial/boot-app?branch=master)

This repository is an example application for Spring Boot and Angular2 tutorial.

[Demo](https://micropost.hana053.com/)

* [Spring Boot](https://projects.spring.io/spring-boot/)
* [Kotlin](https://kotlinlang.org/)
* [jOOQ](https://www.jooq.org/)
* [Flyway](https://flywaydb.org/)
* JWT

## Getting Started

Run Spring Boot.

```
./gradlew jooqGenerate bootRun
```

Serve frontend app.

```
git clone https://github.com/springboot-angular2-tutorial/angular2-app.git
# Follow the README
```

Testing.

```
./gradlew jooqGenerate # If you have not generated jOOQ code yet.
./gradlew test
```

API documentation.

```
./gradlew bootRun
open http://localhost:8080/swagger-ui.html
```

After you migrated DB.
```
./gradlew jooqGenerate # It will generate jOOQ code for your new schema.
```

## Frequently asked questions

* Q) IntelliJ IDEA is very slow when you use jOOQ with Kotlin.
  * A) Refer [this ticket](https://youtrack.jetbrains.com/issue/KT-10978). In my case, [tuning memory config](https://youtrack.jetbrains.com/issue/KT-10978#comment=27-1519260) of IntelliJ IDEA worked.

* Q) How can I run or debug app from IntelliJ IDEA?
  * A) Use IntelliJ IDEA 2017.1 and run or debug Application.kt.

## Docker Support

Dev

```bash
./gradlew clean jooqGenerate build -x test
docker build -t YOUR_IMAGE_NAME .
docker run -p 8080:8080 YOUR_IMAGE_NAME
```

Prod

```bash
./gradlew clean jooqGenerate build -x test
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
