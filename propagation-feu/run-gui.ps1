# Script de compilation et lancement de la version graphique JavaFX

param(
    [string]$JavaFxPath = ""
)

Write-Host "=== Compilation version graphique JavaFX ==="

# Si le chemin JavaFX n'est pas donné en paramètre, on essaie une variable d'environnement
if ([string]::IsNullOrWhiteSpace($JavaFxPath)) {
    $JavaFxPath = $env:JAVAFX_PATH
}

# Si aucun chemin n'est trouvé, on affiche une erreur claire
if ([string]::IsNullOrWhiteSpace($JavaFxPath)) {
    Write-Host "Erreur : chemin JavaFX non défini." -ForegroundColor Red
    Write-Host "Utilisation possible :"
    Write-Host '.\run-gui.ps1 -JavaFxPath "C:\chemin\vers\javafx-sdk-21.0.11\lib"'
    Write-Host ""
    Write-Host "Ou définir une variable d'environnement temporaire :"
    Write-Host '$env:JAVAFX_PATH = "C:\chemin\vers\javafx-sdk-21.0.11\lib"'
    exit 1
}

# Vérification du dossier JavaFX
if (-not (Test-Path $JavaFxPath)) {
    Write-Host "Erreur : le dossier JavaFX n'existe pas :" -ForegroundColor Red
    Write-Host $JavaFxPath
    exit 1
}

# Vérification des fichiers JavaFX nécessaires
$requiredFiles = @(
    "javafx.controls.jar",
    "javafx.fxml.jar",
    "javafx.graphics.jar",
    "javafx.base.jar"
)

foreach ($file in $requiredFiles) {
    $path = Join-Path $JavaFxPath $file
    if (-not (Test-Path $path)) {
        Write-Host "Erreur : fichier JavaFX manquant : $file" -ForegroundColor Red
        Write-Host "Vérifie que le chemin pointe bien vers le dossier lib du SDK JavaFX."
        exit 1
    }
}

# Nettoyage du dossier out
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null

# Récupération des fichiers Java
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }

# Compilation JavaFX
javac --module-path $JavaFxPath --add-modules javafx.controls,javafx.fxml -d out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur pendant la compilation." -ForegroundColor Red
    exit $LASTEXITCODE
}

# Copie du fichier FXML
if (Test-Path "src\main\view.fxml") {
    New-Item -ItemType Directory -Force "out\main" | Out-Null
    Copy-Item "src\main\view.fxml" "out\main\view.fxml" -Force
} else {
    Write-Host "Attention : fichier src\main\view.fxml introuvable." -ForegroundColor Yellow
}

# Copie du fichier CSS
if (Test-Path "src\main\Style.css") {
    New-Item -ItemType Directory -Force "out\main" | Out-Null
    Copy-Item "src\main\Style.css" "out\main\Style.css" -Force
} else {
    Write-Host "Attention : fichier src\main\Style.css introuvable." -ForegroundColor Yellow
}

Write-Host "=== Lancement version graphique JavaFX ==="

# Lancement JavaFX
java --module-path $JavaFxPath --add-modules javafx.controls,javafx.fxml -cp out main.Main