FROM openjdk:11.0.9-slim-buster

# setting /home as working directory
WORKDIR /home

# setting up app directory
USER root
RUN mkdir app

# adding jar
ARG JAR_FILE
ADD ${JAR_FILE} app/app.jar

# adding/setting entry-point script
ADD .docker/start-app.sh start-app.sh
ENTRYPOINT exec sh start-app.sh

#labeling image
LABEL version="1.0" \
      maintainer="abdelrahman@neurox.dev"
