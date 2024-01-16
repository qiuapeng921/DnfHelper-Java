@echo off
chcp 65001
setlocal enabledelayedexpansion

rem 设置目标目录
set "javaDir=D:\Java"
set "targetDirectory=%javaDir%\jdk-17.0.2"

rem 检查目录是否存在
if exist "%targetDirectory%\bin\java.exe" (
    echo Java 已经存在，无需下载。
) else (
    echo 正在下载 JDK 到 %targetDirectory%...
    
    rem 下载 JDK，可以替换为实际的下载链接
    powershell -Command "& { Invoke-WebRequest -Uri 'https://download.java.net/java/GA/jdk17.0.2/dfd4a8d0985749f896bed50d7138ee7f/8/GPL/openjdk-17.0.2_windows-x64_bin.zip' -OutFile 'jdk.zip' }"
    
    rem 解压缩下载的 JDK 到目标目录
    powershell -Command "& { Expand-Archive -Path 'jdk.zip' -DestinationPath '%javaDir%' }"
    
    rem 删除下载的 zip 文件
    del jdk.zip
    
    echo JDK 下载并解压完成。
)

rem 设置环境变量
set "JAVA_HOME=%targetDirectory%"
set "PATH=%JAVA_HOME%\bin;%PATH%"

echo 环境变量已设置完成。

java --version

java -jar Dnfhelper.jar

endlocal
