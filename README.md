# Spring Boot example app for Spring Boot and Angular2 Tutorial
 
[![Build Status][travis-image]][travis-url]
[![Coverage Status](https://coveralls.io/repos/github/springboot-angular2-tutorial/boot-app/badge.svg?branch=master)](https://coveralls.io/github/springboot-angular2-tutorial/boot-app?branch=master)

This repository is an example application for Spring Boot and Angular2 tutorial.

[Demo](https://micropost.hana053.com/).

* JWT
* [Querydsl](http://www.querydsl.com/)
* [Spock](http://spockframework.org/)

## Getting Started

Run Spring Boot.

```
mvn spring-boot:run
```

Serve frontend app.

```
git clone https://github.com/springboot-angular2-tutorial/angular2-app.git
# Follow the README
```

Testing.

```
mvn test
```

API documentation

```
mvn spring-boot:run
open http://localhost:8080/swagger-ui.html
```

### Important note

Before you open this project from Intellij IDEA, you need to build project once. Or else, generated source by annotation processor won't be recognized correctly by IDEA.

```
# It will generate target directory
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true
# After that, open this project from Intellij IDEA.
```

## Docker Support

```bash
mvn clean package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t IMAGE .
docker run -e "SPRING_PROFILES_ACTIVE=prod" \
  -e "MYSQL_ENDPOINT=dbhost:3306" \
  -e "NEW_RELIC_LICENSE_KEY=newrelic licence key" \
  IMAGE
```

## Tutorial

Under construction...

## Related Projects

* [Angular2 app](https://github.com/springboot-angular2-tutorial/angular2-app)
* [Android app](https://github.com/springboot-angular2-tutorial/android-app)
* [Server provisioning by Ansible and Packer](https://github.com/springboot-angular2-tutorial/micropost-provisionings)
* [Infrastructure by Terraform](https://github.com/springboot-angular2-tutorial/micropost-formation)
* [Lambda functions by Serverless](https://github.com/springboot-angular2-tutorial/micropost-functions)

## Credits

* [Rails tutorial](https://github.com/railstutorial/sample_app_rails_4)

## License

[MIT](/LICENSE)

[travis-url]: https://travis-ci.org/springboot-angular2-tutorial/boot-app
[travis-image]: https://travis-ci.org/springboot-angular2-tutorial/boot-app.svg