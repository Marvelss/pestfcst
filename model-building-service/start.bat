@echo off
chcp 65001
echo 正在启动模型构建服务...

cd %~dp0
call mvn clean package -Dmaven.test.skip=true

echo 使用java -jar命令启动服务...
java -Xms512m -Xmx1024m -jar target\model-building-service-0.0.1-SNAPSHOT.jar

pause 