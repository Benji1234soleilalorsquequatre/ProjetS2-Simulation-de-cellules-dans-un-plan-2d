# Script to compile and launch the console version

Write-Host "=== Compilation console version ==="

# Clean the out directory
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null

# Retrieve the Java files required for the console version
# Only JavaFX files are excluded, while ConsoleMain.java is kept
$files = @()

$files += Get-ChildItem -Path .\src\model -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$files += Get-ChildItem -Path .\src\simulation -Recurse -Filter *.java | ForEach-Object { $_.FullName }
$files += ".\src\main\ConsoleMain.java"

# Check that at least one Java file was found
if ($files.Count -eq 0) {
    Write-Host "Error: no Java file found for the console version." -ForegroundColor Red
    exit 1
}

# Compile the console version
javac -d out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Error during compilation." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "=== Launching console version ==="

# Launch the console main class
java -cp out main.ConsoleMain