# Script de compilation et lancement de la version console

Write-Host "=== Compilation version console ==="

# Nettoyage du dossier out
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out | Out-Null

# Récupération des fichiers Java nécessaires pour la console
# On exclut uniquement les fichiers JavaFX, mais on garde ConsoleMain.java
$files = Get-ChildItem -Recurse -Filter *.java src |
    Where-Object {
        $_.FullName -notmatch "\\src\\main\\Main\.java$" -and
        $_.FullName -notmatch "\\src\\main\\Controller\.java$"
    } |
    ForEach-Object { $_.FullName }

# Vérification
if ($files.Count -eq 0) {
    Write-Host "Erreur : aucun fichier Java trouvé pour la version console." -ForegroundColor Red
    exit 1
}

# Compilation
javac -d out $files

if ($LASTEXITCODE -ne 0) {
    Write-Host "Erreur pendant la compilation." -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "=== Lancement version console ==="

# Lancement du main console
java -cp out main.ConsoleMain