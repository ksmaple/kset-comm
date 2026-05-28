# 将 Codex / Cursor 技能目录链接到 .claude/skills（单源真相）
# 在仓库根目录执行: .\scripts\setup-ai-env-links.ps1

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

$source = Join-Path $root '.claude\skills'
if (-not (Test-Path $source)) {
    Write-Error "Missing source: $source"
}

function Ensure-DirLink {
    param(
        [string]$LinkPath,
        [string]$TargetPath
    )
    $parent = Split-Path -Parent $LinkPath
    if ($parent -and -not (Test-Path $parent)) {
        New-Item -ItemType Directory -Path $parent -Force | Out-Null
    }
    if (Test-Path $LinkPath) {
        $item = Get-Item $LinkPath -Force
        if ($item.Attributes -band [IO.FileAttributes]::ReparsePoint) {
            $currentTarget = $item.Target
            if ($currentTarget -and ($currentTarget -ieq $TargetPath)) {
                Write-Host "OK (exists): $LinkPath"
                return
            }
            Write-Host "Relink (stale target): $LinkPath -> $currentTarget"
            Remove-Item -LiteralPath $LinkPath -Force
        } else {
            $children = Get-ChildItem -LiteralPath $LinkPath -Force -ErrorAction SilentlyContinue
            if ($children -and $children.Count -gt 0) {
                Write-Error "Path exists and is not empty (not a link): $LinkPath"
            }
            Remove-Item -LiteralPath $LinkPath -Force
            Write-Host "Removed empty dir: $LinkPath"
        }
    }
    cmd /c mklink /J `"$LinkPath`" `"$TargetPath`" | Out-Null
    Write-Host "Linked: $LinkPath -> $TargetPath"
}

Ensure-DirLink -LinkPath (Join-Path $root '.agents\skills') -TargetPath $source
Ensure-DirLink -LinkPath (Join-Path $root '.cursor\skills') -TargetPath $source
Write-Host 'Done. Skills source: .claude/skills'
