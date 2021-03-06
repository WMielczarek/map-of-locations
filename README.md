# Literary Map 
Application alows users draw location on map found in text. 
Application finds location in text using ws.clarin.pl API. 
Also using nominatim API to find GeoJSON of found locations.


##Prerequisites
Installed 

* docker
* docker-compose
* mvn 
* Java 11 as default Java version
* Vue
* Vue materials

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
82784a35211a        literary-map-client   ""                       9 seconds ago       Up 5 seconds        0.0.0.0:8081->8081/tcp     literary-map-client


```
## Use nominatim container
Nominatim API has restriction in numbers of request send in one second. To avoid ban on nominatim API, use nominatim container.

## Application demo

Main page view.
![Main page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/Home.png)

New project view.
![NewProject page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/NewProject.png)

New project analyze view.
![NewProjectAnalyze page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/Analyze.png)

Open project view.
![NewProjectAnalyze page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/OpenProject.png)

Map view.
![NewProjectAnalyze page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/Map.png)

Edit location view.
![NewProjectAnalyze page](https://github.com/WMielczarek/map-of-locations/blob/master/jpgs/EditLocation.png)







