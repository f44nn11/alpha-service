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


✅ Cara Aktifkan Auto-Build Saat Aplikasi Berjalan (Versi 2024.x)
🔹 Langkah 1: Aktifkan Auto-Build
Masuk ke Settings > Build, Execution, Deployment > Compiler

Aktifkan:

✅ Build project automatically

🔹 Langkah 2: Aktifkan Continuous Build lewat Advanced Settings
Buka menu: File > Settings > Advanced Settings

Cari bagian Compiler

Pastikan centang:

✅ compiler.automake.allow.parallel

✅ compiler.automake.build.while.idle.timeout → diset misalnya 1000 atau 3000 ms

Artinya: kalau kamu diam 1-3 detik, IntelliJ akan otomatis compile project meskipun app sedang jalan.

##CraeteFolder host
sudo mkdir -p /document/support/dev

##Mounting Folder
sudo mount -t cifs //192.168.2.2/ALPHA-APP/document/support/dev  /document/support/dev -o username=appsvr,password=Alphaproject24,vers=3.0
sudo mount -t cifs //192.168.2.2/ALPHA-APP/document/support/prod  /document/support/prod -o username=userapp,password=Alphaproject24,vers=3.0

##Fstab
//192.168.2.2/ALPHA-APP/document/support/dev /document/support/dev cifs username=userapp,password=Alphaproject24,vers=3.0 0 0
//192.168.2.2/ALPHA-APP/document/support/prod /document/support/prod cifs username=userapp,password=Alphaproject24,vers=3.0 0 0

id


sudo mount -t cifs \
-o username=appsvr,password=Alphaproject24,uid=1000,gid=1000,file_mode=0775,dir_mode=0775 \
//192.168.2.2/ALPHA-APP/document/support/dev \
/mnt/alpha-support/dev

##Fstab
//192.168.2.2/ALPHA-APP/document/support/dev  /mnt/alpha-support/dev  cifs  username=userapp,password=Alphaproject24,uid=1000,gid=1000,file_mode=0775,dir_mode=0775  0  0