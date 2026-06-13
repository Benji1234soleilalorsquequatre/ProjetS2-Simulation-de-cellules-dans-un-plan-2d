# Script to compile and launch the JavaFX graphical version

param(
    [string]$JavaFxPath = ""
)

Write-Host "=== Compilation JavaFX graphical version ==="

# If the JavaFX path is not provided as a parameter, try to use an environment variable
if ([string]::IsNullOrWhiteSpace($JavaFxPath)) {
    $JavaFxPath = $env:JAVAFX_PATH
}

# If no JavaFX path is found, display a clear error message
if ([string]::IsNullOrWhiteSpace($JavaFxPath)) {
    Write-Host "Error: JavaFX path is not defined." -ForegroundColor Red
    Write-Host "Possible usage:"
    Write-Host '.\run-gui.ps1 -JavaFxPath "C:\path\to\javafx-sdk-21.0.11\lib"'
    Write-Host ""
    Write-Host "Or define a temporary environment variable:"
    Write-Host '$env:JAVAFX_PATH = "C:\path\to\javafx-sdk-21.0.11\lib"'
    exit 1
}

# Check that the JavaFX directory exists
if (-not (Test-Path $JavaFxPath)) {
    Write-Host "Error: the JavaFX directory does not exist:" -ForegroundColor Red
    Write-Host $JavaFxPath
    exit 1
}

# Check that the required JavaFX files are present
$requiredFiles = @(
    "javafx.controls.jar",
    "javafx.fxml.jar",
    "javafx.graphics.jar",
    "javafx.base.jar"
)

foreach ($file in $requiredFiles) {
    $path = Join-Path $JavaFxPath $file
    if (-not (Test-Path $path)) {
        Write-Host "Error: missing JavaFX file: $file" -ForegroundColor Red
        Write-Host "Make sure the path points to the lib directory of the JavaFX SDK."
        exit 1
    }
}

# Clean the out directory
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null

# Retrieve all Java source files
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }

# Compile the JavaFX version
javac --module-path $JavaFxPath --add-modules javafx.controls,javafx.fxml -d out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error during compilation." -ForegroundColor Red
    exit $LASTEXITCODE
}

# Copy the FXML file
if (Test-Path "src\main\view.fxml") {
    New-Item -ItemType Directory -Force "out\main" | Out-Null
    Copy-Item "src\main\view.fxml" "out\main\view.fxml" -Force
} else {
    Write-Host "Warning: file src\main\view.fxml not found." -ForegroundColor Yellow
}

# Copy the CSS file
if (Test-Path "src\main\Style.css") {
    New-Item -ItemType Directory -Force "out\main" | Out-Null
    Copy-Item "src\main\Style.css" "out\main\Style.css" -Force
} else {
    Write-Host "Warning: file src\main\Style.css not found." -ForegroundColor Yellow
}

Write-Host "=== Launching JavaFX graphical version ==="

# Launch the JavaFX application
java --module-path $JavaFxPath --add-modules javafx.controls,javafx.fxml -cp out main.Main