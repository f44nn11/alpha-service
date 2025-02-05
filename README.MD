## Testing repo
## Push Docker
docker login 

1. docker tag alpha-service:latest alphatech202/alphaproject1:alpha-service
2. docker push alphatech2024/alphaproject1:alpha-service

docker run -d \
--name mariadb-alpha \
-e MARIADB_ROOT_PASSWORD=alpha24 \
-e MARIADB_DATABASE=app_db \
-e MARIADB_USER=app_user \
-e MARIADB_PASSWORD=app_pass \
-v D:/PROJECT/MARIADB:/var/lib/mysql \
-p 3306:3306 \
mariadb:latest

docker exec -it my-mariadb bash
ln -sf /usr/share/zoneinfo/Asia/Jakarta /etc/localtime