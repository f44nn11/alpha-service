@echo off
echo Menjalankan alpha-service dan sysprop-master...

:: Jalankan alpha-service di port 8081
start cmd /k "cd /d E:\PROJECT\ALPHA\alpha-service && call mvnw spring-boot:run -Dspring-boot.run.profiles=dev"

:: Jalankan sysprop-master di port 8082
start cmd /k "cd /d E:\PROJECT\ALPHA\sysprop-master && call mvnw spring-boot:run -Dspring-boot.run.profiles=local"

echo Menunggu service startup...
timeout /t 20 >nul

echo Membuka status health check di browser...
start http://localhost:8081/actuator/health
start http://localhost:8082/actuator/health

pause