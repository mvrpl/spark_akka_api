# Akka API with Apache Spark on Docker
## Docker
#### Generate a Docker image:
```sh
docker build -t api_spark:1.0 .
```
#### Running a Docker container:
```sh
docker run --user mvrpl -p 5001:5001 -it api_spark:1.0 /bin/bash
```
## Spark & HTTP Akka Application
#### Build application:
```sh
cd app
sbt 'set test in assembly := {}' clean assembly
```
#### Running application:
```sh
spark-submit --master local[*] --class WebServer target/scala-2.11/WebServer-assembly-1.0.jar
```
#### Testing the application:
```sh
curl -d "busca=hello" -X POST http://127.0.0.1:5001/wc
```
License
----
MIT
