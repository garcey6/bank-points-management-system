@echo off

echo ====================================
echo 银行消费积分管理系统 - 一键安装脚本
echo ====================================

rem 检查Node.js是否安装
where node >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未检测到Node.js，请先安装Node.js 14.0.0或更高版本
    echo 下载地址: https://nodejs.org/zh-cn/download/
    pause
    exit /b 1
)

rem 检查npm是否安装
where npm >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未检测到npm，请先安装Node.js 14.0.0或更高版本
    echo 下载地址: https://nodejs.org/zh-cn/download/
    pause
    exit /b 1
)

rem 检查Java是否安装
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未检测到Java，请先安装JDK 1.8或更高版本
    echo 下载地址: https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html
    pause
    exit /b 1
)

rem 检查Maven是否安装
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo 错误: 未检测到Maven，请先安装Maven 3.6.0或更高版本
    echo 下载地址: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo 正在安装前端依赖...
cd frontend
call npm install
if %errorlevel% neq 0 (
    echo 错误: 前端依赖安装失败
    pause
    exit /b 1
)
echo 前端依赖安装成功！

cd ..
echo 正在构建后端项目...
cd backend
call mvn clean install
if %errorlevel% neq 0 (
    echo 错误: 后端项目构建失败
    pause
    exit /b 1
)
echo 后端项目构建成功！

cd ..
echo ====================================
echo 一键安装完成！
echo ====================================
echo 运行说明:
echo 1. 前端运行: 进入frontend目录，执行 npm run serve
echo 2. 后端运行: 进入backend目录，执行 mvn spring-boot:run
echo 3. 前端访问: http://localhost:8083
echo 4. 后端访问: http://localhost:8081
echo ====================================
pause