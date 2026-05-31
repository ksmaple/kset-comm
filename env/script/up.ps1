param(
    [switch]$Build,
    [switch]$NoCat
)

$ErrorActionPreference = "Stop"

$ScriptDir = $PSScriptRoot
$EnvDir = Split-Path $ScriptDir -Parent

& (Join-Path $ScriptDir "sync.ps1")

$envFile = Join-Path $EnvDir ".env"
if (-not (Test-Path $envFile)) {
    throw "Missing env/.env. Run: Copy-Item env/.env.example env/.env"
}

Push-Location $EnvDir
try {
    $composeBase = @("compose", "--env-file", ".env", "-f", "docker-compose.yml")
    if (-not $NoCat) {
        $composeBase += @("-f", "cat/docker-compose.yml")
    }

    $upArgs = $composeBase + @("up", "-d")
    if ($Build) {
        $upArgs += "--build"
    }

    Write-Host "docker $($upArgs -join ' ')"
    & docker @upArgs
    & docker @($composeBase + @("ps"))
}
finally {
    Pop-Location
}
