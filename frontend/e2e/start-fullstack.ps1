param()

$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $PSCommandPath
$frontendDir = Split-Path -Parent $scriptDir
$repoRoot = Split-Path -Parent $frontendDir

Push-Location $frontendDir
try {
  npm ci
  npm run build
} finally {
  Pop-Location
}

$staticPath = Join-Path $repoRoot "backend\src\main\resources\static"
if (Test-Path $staticPath) {
  Remove-Item -Recurse -Force $staticPath
}
New-Item -ItemType Directory -Force $staticPath | Out-Null

$distPath = Join-Path $frontendDir "dist"
Copy-Item -Recurse -Force (Join-Path $distPath "*") $staticPath

$storageDir = Join-Path ([System.IO.Path]::GetTempPath()) ("kombaos_e2e_" + [Guid]::NewGuid().ToString("N"))

Push-Location (Join-Path $repoRoot "backend")
try {
  $env:ENVIRONMENT = "local"
  $env:SERVER_PORT = "8081"
  $env:KOMBAOS_LOCAL_STORAGE_DIR = $storageDir
  $env:DATABASE_URL = "jdbc:h2:mem:kombaos_e2e_" + [Guid]::NewGuid().ToString("N") + ";MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
  .\mvnw.cmd -q -DskipTests spring-boot:run
} finally {
  Pop-Location
}
