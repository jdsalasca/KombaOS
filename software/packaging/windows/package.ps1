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

function Invoke-Json {
  param(
    [Parameter(Mandatory=$true)][ValidateSet("GET","POST","PUT","DELETE")][string]$Method,
    [Parameter(Mandatory=$true)][string]$Url,
    [object]$Body = $null,
    [int]$TimeoutSec = 5
  )

  if ($null -ne $Body) {
    $json = $Body | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Uri $Url -Method $Method -ContentType "application/json" -Body $json -TimeoutSec $TimeoutSec
  }

  return Invoke-RestMethod -Uri $Url -Method $Method -TimeoutSec $TimeoutSec
}

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
.\mvnw.cmd -DskipTests package
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

  $storageDir = Join-Path ([System.IO.Path]::GetTempPath()) ("kombaos_smoke_" + [Guid]::NewGuid().ToString("N"))
  $dbUrl = "jdbc:h2:file:" + (Join-Path $storageDir "db") + ";MODE=PostgreSQL;AUTO_SERVER=TRUE"

  Write-Host "Smoke test: iniciando $exePath"
  $proc = Start-Process -FilePath $exePath -WorkingDirectory $distDir -PassThru -Environment @{
    "KOMBAOS_LOCAL_STORAGE_DIR" = $storageDir
    "DATABASE_URL" = $dbUrl
  }
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

    $material = Invoke-Json -Method POST -Url "http://localhost:8080/api/materials" -Body @{ name = "Lana"; unit = "kg" } -TimeoutSec 5
    if (-not $material.id) { throw "Smoke test: material.id vacío" }

    Invoke-Json -Method POST -Url "http://localhost:8080/api/inventory/movements" -Body @{ materialId = $material.id; type = "IN"; quantity = 1.5; reason = "Smoke" } -TimeoutSec 5 | Out-Null

    $stock = Invoke-Json -Method GET -Url ("http://localhost:8080/api/materials/" + $material.id + "/stock") -TimeoutSec 5
    if ($stock.stock -lt 1.5) { throw "Smoke test: stock inesperado" }
    Write-Host "Smoke test: OK (material+movement+stock)"

    Invoke-Json -Method PUT -Url ("http://localhost:8080/api/materials/" + $material.id + "/threshold") -Body @{ minStock = 10 } -TimeoutSec 5 | Out-Null
    $alerts = Invoke-Json -Method GET -Url "http://localhost:8080/api/inventory/alerts/low-stock" -TimeoutSec 5
    $isLow = $false
    foreach ($a in $alerts) { if ($a.materialId -eq $material.id) { $isLow = $true } }
    if (-not $isLow) { throw "Smoke test: se esperaba alerta de stock bajo" }

    Invoke-Json -Method POST -Url "http://localhost:8080/api/inventory/movements" -Body @{ materialId = $material.id; type = "IN"; quantity = 20; reason = "Smoke" } -TimeoutSec 5 | Out-Null
    $alerts2 = Invoke-Json -Method GET -Url "http://localhost:8080/api/inventory/alerts/low-stock" -TimeoutSec 5
    $stillLow = $false
    foreach ($a in $alerts2) { if ($a.materialId -eq $material.id) { $stillLow = $true } }
    if ($stillLow) { throw "Smoke test: no se esperaba alerta de stock bajo después del ingreso" }

    Write-Host "Smoke test: OK (threshold+alerts)"
  } finally {
    if (-not $proc.HasExited) { Stop-Process -Id $proc.Id -Force }
  }
}
