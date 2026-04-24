$envFile = Get-Content -Path ".env"
foreach ($line in $envFile) {
    if ($line -match "^[^#].*=.*") {
        $name, $value = $line -split "=", 2
        [System.Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim(), [System.EnvironmentVariableTarget]::Process)
    }
}

.\mvnw.cmd test "-Dtest=AdServiceTest"
