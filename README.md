# Fun With Flags

This multiplayer "Guess the country/territory/organization of a flag" game was created for a presentation about [HTMX](https://htmx.org).

HTMX is a JavaScript library for making rich client applications using just server-side rendered HTML. 

Quarkus is used for the backend.

The game uses websockets, AJAX and just a plain HTML backend.

Technologies used:

- [HTMX](https://htmx.org)
- [HTMX web-sockets](https://htmx.org/extensions/web-sockets/)
- [Bootstrap 5.3](https://getbootstrap.com/docs/5.3/getting-started/)
- [Flag icons](https://flagicons.lipis.dev)
- [Quarkus](https://quarkus.io)
- [Resteasy](https://quarkus.io/extensions/io.quarkus/quarkus-resteasy-qute)
- [Web Sockets](https://quarkus.io/extensions/io.quarkus/quarkus-websockets)
- [Qute](https://quarkus.io/extensions/io.quarkus/quarkus-qute)
- [Scheduler](https://quarkus.io/extensions/io.quarkus/quarkus-scheduler)

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/fwf-1.0.0-SNAPSHOT-runner`
