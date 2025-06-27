@echo off
chcp 65001
echo 正在启动特征优化服务...
call mvn clean compile
call mvn spring-boot:run -Dspring-boot.run.arguments=--debug -Dspring-boot.run.fork=false
echo 服务已停止运行，按任意键关闭窗口...
pause 