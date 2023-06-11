Currency Converter
==================

Currency Converter is a RESTful webservice built using Spring Boot. It exposes an endpoint that allows you to convert an
arbitrary amount of a given base currency into a specified target currency. The web service fetches the latest currency
conversion rates from various third-party services. You can choose which third-party service to use for fetching
conversion rates through Spring profiles.

Requirements
------------

- Java 17
- Maven 3.x

### Optional

- Docker

Build
-----

To build the application, navigate to the root directory where `pom.xml` is located and run the following command:

```sh
./mvnw clean install
```

Run
---

There are two ways to run the application:

### 1\. Using Java:

Run the following command:

```sh
java -jar target/challenge-0.0.1-SNAPSHOT.jar
```

#### Optional Arguments

- `--spring.profiles.active=era`

    - This will use [ExchangeRatesAPI](https://exchangeratesapi.io/) to fetch currency conversion rates. (This is the
      default profile)
- `--spring.profiles.active=cl`

    - This will use [CurrencyLayer](https://currencylayer.com/) to fetch currency conversion rates.
- `--debug=true`

    - This will change the log level to DEBUG (default is INFO).

### 2\. Using Docker:

Navigate to the root folder and build the image with the following command:

```sh
docker build -t tml-coding-challenge .
```

Create a container and start it using the following command:

```sh
docker run -p 8080:8080 -d tml-coding-challenge
```

#### Optional Arguments

- `-e SPRING_PROFILES_ACTIVE=era`

    - This will use [ExchangeRatesAPI](https://exchangeratesapi.io/) to fetch currency conversion rates. (This is the
      default profile)
- `-e SPRING_PROFILES_ACTIVE=cl`

    - This will use [CurrencyLayer](https://currencylayer.com/) to fetch currency conversion rates.
- `-e DEBUG=true`

    - This will change the log level to DEBUG (default is INFO).

Please note that `-p 8080:8080` is required as it binds the local port 8080 to the container port 8080.

Swagger Documentation
---------------------

To check the API documentation, navigate to the following URL:

```
http://localhost:8080/swagger-ui.html
```

Replace `localhost` and `8080` with the appropriate host and port if you are running the application on a different host
or port.