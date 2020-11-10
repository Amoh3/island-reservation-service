./filebeat/filebeat -e -c /home/filebeat/filebeat.yml > /home/filebeat.log &
java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=docker -jar app/app.jar
