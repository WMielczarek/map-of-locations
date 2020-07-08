# Literary Map Server

##Prerequisites
Installed 

* docker
* docker-compose
* mvn 

with access from CommandLine (Windows) / Terminal (Linux)

## How to run?
To run automated process of whole docker environment setup, use  ``make`` from root of project repository.

## How to check whether containers are correctly setup?
Run ``docker ps`` which should return all containers running on system.

Expected response:
```
CONTAINER ID        IMAGE                 COMMAND                  CREATED             STATUS              PORTS                      NAMES
4a05f44daeb9        literary-map-server   "java -jar /app.jar"     9 seconds ago       Up 8 seconds        0.0.0.0:8080->8080/tcp     literary-map-server
82784a898738        mongo                 "docker-entrypoint.s…"   9 seconds ago       Up 5 seconds        0.0.0.0:27017->27017/tcp   mongo-db

```