param(
  [ValidateSet("local","cloud")]
  [string]$Environment = "local",
  [switch]$SmokeTest,
  [int]$SmokeTestTimeoutSeconds = 45
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$root = Split-Path -Parent $root
$root = Split-Path -Parent $root

$frontendDir = Join-Path $root "frontend"
$backendDir = Join-Path $root "backend"
$backendStaticDir = Join-Path $backendDir "src\\main\\resources\\static"
$distDir = Join-Path $root "dist"

if (Test-Path $distDir) { Remove-Item -Recurse -Force $distDir }
New-Item -ItemType Directory -Force -Path $distDir | Out-Null

Push-Location $frontendDir
npm ci
npm run build
Pop-Location

if (Test-Path $backendStaticDir) { Remove-Item -Recurse -Force $backendStaticDir }
New-Item -ItemType Directory -Force -Path $backendStaticDir | Out-Null
Copy-Item -Recurse -Force (Join-Path $frontendDir "dist\\*") $backendStaticDir

Push-Location $backendDir
./mvnw -DskipTests package
$jar = Get-ChildItem -Path (Join-Path $backendDir "target") -Filter "*.jar" | Where-Object { $_.Name -notmatch "original" } | Select-Object -First 1
if (-not $jar) { throw "No se encontró JAR en backend\\target" }

jpackage `
  --type app-image `
  --name "KombaOS" `
  --app-version "0.1.0" `
  --input (Join-Path $backendDir "target") `
  --main-jar $jar.Name `
  --dest $distDir `
  --java-options "-Dkombaos.environment=$Environment" `
  --win-console
Pop-Location

if ($SmokeTest) {
  $exePath = Join-Path $distDir "KombaOS\\KombaOS.exe"
  if (-not (Test-Path $exePath)) { throw "No se encontró el ejecutable: $exePath" }

  Write-Host "Smoke test: iniciando $exePath"
  $proc = Start-Process -FilePath $exePath -WorkingDirectory $distDir -PassThru
  try {
    $deadline = (Get-Date).AddSeconds($SmokeTestTimeoutSeconds)
    $lastError = $null
    while ((Get-Date) -lt $deadline) {
      if ($proc.HasExited) { throw "KombaOS.exe terminó con exit code $($proc.ExitCode)" }
      try {
        $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 2
        if ($health.status -eq "UP") { break }
      } catch {
        $lastError = $_
        Start-Sleep -Milliseconds 500
      }
    }

    $health = Invoke-RestMethod -Uri "http://localhost:8080/actuator/health" -Method GET -TimeoutSec 2
    if ($health.status -ne "UP") { throw "Healthcheck no está UP" }
    Write-Host "Smoke test: OK (actuator/health=UP)"

    $materialBody = @{ name = "Lana"; unit = "kg" } | ConvertTo-Json
    $material = Invoke-RestMethod -Uri "http://localhost:8080/api/materials" -Method POST -ContentType "application/json" -Body $materialBody -TimeoutSec 5
    if (-not $material.id) { throw "Smoke test: material.id vacío" }

    $moveBody = @{ materialId = $material.id; type = "IN"; quantity = 1.5; reason = "Smoke" } | ConvertTo-Json
    Invoke-RestMethod -Uri "http://localhost:8080/api/inventory/movements" -Method POST -ContentType "application/json" -Body $moveBody -TimeoutSec 5 | Out-Null

    $stock = Invoke-RestMethod -Uri ("http://localhost:8080/api/materials/" + $material.id + "/stock") -Method GET -TimeoutSec 5
    if ($stock.stock -lt 1.5) { throw "Smoke test: stock inesperado" }
    Write-Host "Smoke test: OK (material+movement+stock)"
  } finally {
    if (-not $proc.HasExited) { Stop-Process -Id $proc.Id -Force }
  }
}
