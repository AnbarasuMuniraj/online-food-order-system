@echo off
setlocal enabledelayedexpansion

echo ===================================================
echo   Starting Online Food Order Processing System
echo ===================================================
echo.

:: 1. Detect and Set JAVA_HOME
if not "%JAVA_HOME%"=="" goto :java_home_set
if exist "C:\Program Files\Java\jdk-24" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    echo Automatically set JAVA_HOME to C:\Program Files\Java\jdk-24
    goto :java_home_set
)
for /d %%D in ("C:\Program Files\Java\jdk-*") do (
    set "JAVA_HOME=%%D"
    echo Automatically set JAVA_HOME to %%D
    goto :java_home_set
)

:java_home_set
if not "%JAVA_HOME%"=="" (
    echo JAVA_HOME is set to: %JAVA_HOME%
) else (
    echo WARNING: JAVA_HOME is not set.
)
echo.

:: 2. Detect Maven
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Maven is available in PATH.
    goto :mvn_detect_done
)

echo Maven (mvn) not found in PATH. Locating...
set "FOUND_PATH="

if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.1.1.1\plugins\maven\lib\maven3\bin\mvn.cmd" (
    set "FOUND_PATH=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.1.1.1\plugins\maven\lib\maven3\bin"
    goto :mvn_found
)

if exist "%USERPROFILE%\.m2\wrapper\dists" (
    for /r "%USERPROFILE%\.m2\wrapper\dists" %%F in (mvn.cmd) do (
        if exist "%%F" (
            set "FOUND_PATH=%%~dpF"
            goto :mvn_found
        )
    )
)

:mvn_found
if not "%FOUND_PATH%"=="" (
    if "%FOUND_PATH:~-1%"=="\" set "FOUND_PATH=%FOUND_PATH:~0,-1%"
    echo Found Maven at: %FOUND_PATH%
    set "PATH=%FOUND_PATH%;%PATH%"
    echo Added Maven to temporary session PATH.
) else (
    echo ERROR: Maven could not be located on this system.
    echo Please install Maven and add it to your PATH environment variable.
    pause
    exit /b 1
)

:mvn_detect_done
echo.

:: 3. Port Check and Status Report
echo Checking existing service instances...

netstat -ano | findstr LISTENING | findstr /C:":3306 " >nul
set "MYSQL_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":61616 " >nul
set "ACTIVEMQ_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":8081 " >nul
set "ORDER_SERVICE_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":8082 " >nul
set "PAYMENT_SERVICE_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":8083 " >nul
set "KITCHEN_SERVICE_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":8084 " >nul
set "DELIVERY_SERVICE_RUNNING=%ERRORLEVEL%"

netstat -ano | findstr LISTENING | findstr /C:":3000 " >nul
set "FRONTEND_RUNNING=%ERRORLEVEL%"

if %MYSQL_RUNNING% neq 0 (
    echo WARNING: MySQL is not running on port 3306!
    echo Please make sure your MySQL database is started before using the services.
) else (
    echo [OK] MySQL database is running on port 3306.
)
echo.

:: 4. Prompt to Build Project
echo ===================================================
echo   Would you like to build the project first?
echo ===================================================
choice /c yn /t 5 /d n /m "Build project with Maven (y/n)?"
if %ERRORLEVEL% equ 1 (
    echo Building the project with Maven...
    call mvn clean install -DskipTests
    if !ERRORLEVEL! neq 0 (
        echo ERROR: Maven build failed.
        pause
        exit /b 1
    )
    echo Build Succeeded!
)
echo.

:: 5. Launch Services
echo Starting services...

:: Start ActiveMQ
if %ACTIVEMQ_RUNNING% neq 0 (
    echo Starting ActiveMQ Broker...
    start "ActiveMQ Broker" cmd /k "cd infrastructure\apache-activemq-5.18.3\bin && activemq.bat start"
    echo Waiting 5 seconds for ActiveMQ to initialize...
    timeout /t 5 >nul
) else (
    echo [Skipped] ActiveMQ is already running on port 61616.
)

:: Start Backend Services
if %PAYMENT_SERVICE_RUNNING% neq 0 (
    echo Starting Payment Service...
    start "Payment Service" cmd /k "cd payment-service && mvn spring-boot:run"
) else (
    echo [Skipped] Payment Service is already running on port 8082.
)

if %KITCHEN_SERVICE_RUNNING% neq 0 (
    echo Starting Kitchen Service...
    start "Kitchen Service" cmd /k "cd kitchen-service && mvn spring-boot:run"
) else (
    echo [Skipped] Kitchen Service is already running on port 8083.
)

if %DELIVERY_SERVICE_RUNNING% neq 0 (
    echo Starting Delivery Service...
    start "Delivery Service" cmd /k "cd delivery-service && mvn spring-boot:run"
) else (
    echo [Skipped] Delivery Service is already running on port 8084.
)

:: Start Order Service (Main)
if %ORDER_SERVICE_RUNNING% neq 0 (
    echo Starting Order Service...
    start "Order Service" cmd /k "cd order-service && mvn spring-boot:run"
) else (
    echo [Skipped] Order Service is already running on port 8081.
)

:: Start React Frontend
if %FRONTEND_RUNNING% neq 0 (
    echo Starting React Frontend...
    start "React Frontend" cmd /k "cd react-frontend && npm run dev"
) else (
    echo [Skipped] React Frontend is already running on port 3000.
)

echo.
echo ===================================================
echo   All components initiated successfully!
echo ===================================================
echo   - Order Service: http://localhost:8081
echo   - Payment Service: http://localhost:8082
echo   - Kitchen Service: http://localhost:8083
echo   - Delivery Service: http://localhost:8084
echo   - React Frontend: http://localhost:3000
echo   - ActiveMQ Console: http://localhost:8161/admin (admin/admin)
echo ===================================================
echo   Press any key to exit this launcher window...
pause >nul
