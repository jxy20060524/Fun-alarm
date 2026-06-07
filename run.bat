@echo off
setlocal
cd /d "%~dp0"

set "MVN="
for /d %%i in ("%LOCALAPPDATA%\JetBrains\IntelliJIdea*") do (
    if exist "%%i\plugins\maven\lib\maven3\bin\mvn.cmd" set "MVN=%%i\plugins\maven\lib\maven3\bin\mvn.cmd"
)
if not defined MVN (
    where mvn >nul 2>&1 && set "MVN=mvn"
)
if not defined MVN (
    echo [错误] 未找到 Maven。请在 IntelliJ 中：右键 pom.xml -^> Maven -^> Reload Project，然后使用运行配置 FunAlarmApp 启动。
    exit /b 1
)

echo 正在下载依赖并启动...
call "%MVN%" -q clean javafx:run
endlocal
