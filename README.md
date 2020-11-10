# Island reservation service

An underwater volcano formed a new small island in the Pacific Ocean. All the conditions on the island seems perfect, and it was
decided to open it up for the public to experience the pristine uncharted territory.
The island is big enough to host a single campsite so everybody is very excited to visit. In order to regulate the number of people on the island, it
was decided to come up with an online web application to manage the reservations. This the backend portion of the reservation system.

## Installation

### Prerequisites
* Maven 3.6+
* Java 11
* Docker with docker compose

## Usage

```bash
mvn clean install
docker build --build-arg JAR_FILE=api/target/api-1.0-SNAPSHOT.jar -t ir-api:1.0
docker-compose up
```

