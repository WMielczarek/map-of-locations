# Soft clean and start containers on newly built image.
start: --build-jar --run-containers --soft-clean-containers

# Clean runtime environment EXPECT database container.
soft-clean: --soft-clean-containers --remove-image

# Clean everything from runtitme env.
hard-clean: --remove-image
	docker-compose down

--build-jar:
	mvn clean package -DskipTests -f ./backend/pom.xml

--run-containers:
	docker-compose up

--soft-clean-containers:
	docker-compose rm -f literary-map-server

--remove-image:
	docker image rm -f literary-map-server
