param(
    [switch]$NoCat,
    [switch]$Volumes
)

$ErrorActionPreference = "Stop"

$ScriptDir = $PSScriptRoot
$EnvDir = Split-Path $ScriptDir -Parent

Push-Location $EnvDir
try {
    $envArgs = @()
    if (Test-Path (Join-Path $EnvDir ".env")) {
        $envArgs = @("--env-file", ".env")
    }

    $composeBase = @("compose") + $envArgs + @("-f", "docker-compose.yml")
    if (-not $NoCat) {
        $composeBase += @("-f", "cat/docker-compose.yml")
    }

    $downArgs = $composeBase + @("down")
    if ($Volumes) {
        $downArgs += "-v"
    }

    Write-Host "docker $($downArgs -join ' ')"
    & docker @downArgs
}
finally {
    Pop-Location
}
