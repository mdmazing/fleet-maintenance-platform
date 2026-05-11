# setup.ps1 — One-time project bootstrap for Windows
# Run this script once to install Maven and generate the Maven Wrapper (mvnw).
# After that you only ever use ./mvnw — no system Maven needed.

Write-Host "==> Checking for Maven..." -ForegroundColor Cyan

if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Maven already installed: $(mvn -version 2>&1 | Select-Object -First 1)" -ForegroundColor Green
} else {
    Write-Host "Maven not found. Installing via winget..." -ForegroundColor Yellow
    winget install Apache.Maven --silent
    Write-Host "Restart this terminal after install, then re-run this script." -ForegroundColor Red
    exit 1
}

Write-Host "==> Generating Maven Wrapper (mvnw / mvnw.cmd)..." -ForegroundColor Cyan
mvn wrapper:wrapper -Dmaven=3.9.9

Write-Host ""
Write-Host "==> Done. You can now use:" -ForegroundColor Green
Write-Host "    docker-compose up -d postgres       # start local database"
Write-Host "    ./mvnw spring-boot:run              # start the app"
Write-Host "    ./mvnw test                         # run unit tests"
Write-Host "    ./mvnw verify                       # run all tests including Testcontainers"
Write-Host ""
Write-Host "    Swagger UI:  http://localhost:8080/swagger-ui.html"
Write-Host "    Health:      http://localhost:8080/actuator/health"
