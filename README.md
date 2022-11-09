# webapp

## APIs
Get more details in ./materials/CSYE7125.postman_collection.json

## Database tables
[flyway migration image](https://hub.docker.com/r/paddlew/csye7125-db)

Initiate your database by running a container based on this image. Set up your database connection through environment variables.


# How to run

## Development

1. Update db connenction parameters in application.yml
2. Run sql scripts to build tables

## Through dockerfile

1. Build an image with Dockerfile
2. Start a container
   1. Set environment variables. If you want to set a env variable with key spring.datasource.url and value jdbc:mysql://localhost:3309/todo. Change the key to SPRING_DATASOURCE_URL and keep the original value.


## Pull Docker Image
1. Search csye7125-web in Docker Hub
2. Pull it and run it