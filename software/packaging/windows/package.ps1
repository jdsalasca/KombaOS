$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
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
  --type exe `
  --name "KombaOS" `
  --app-version "0.1.0" `
  --input (Join-Path $backendDir "target") `
  --main-jar $jar.Name `
  --dest $distDir `
  --win-console
Pop-Location
