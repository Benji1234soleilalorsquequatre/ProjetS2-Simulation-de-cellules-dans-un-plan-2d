# Simulation de propagation du feu dans une grille 2D

Le projet contient une version en ligne de commande permettant de tester le modèle de simulation, ainsi qu'une version graphique JavaFX destinée à l'utilisation finale.

## Objectif du projet

L'application simule l'évolution d'un feu sur une grille composée de cellules. Chaque cellule peut représenter un état différent, par exemple de la végétation, une cellule en feu ou une cellule brûlée.

La simulation peut prendre en compte différents paramètres comme :
- l'état des cellules voisines ;
- le type de végétation ;
- le vent ;
- des probabilités de propagation ;
- des interactions utilisateur comme larguer de l'eau sur la grille.

## Structure du projet

```text
ProjetS2-Simulation-de-cellules-dans-un-plan-2d-main/
│
├── README.md
├── Rapport simulation 2D propagation de feu.pdf
│
└── propagation-feu/
    │
    ├── src/
    │   ├── display/
    │   │   ├── Camera.java
    │   │   └── DisplayManager.java
    │   │
    │   ├── model/
    │   │   ├── Cell.java
    │   │   ├── Grid.java
    │   │   ├── State.java
    │   │   ├── Vegetation.java
    │   │   └── Wind.java
    │   │
    │   ├── simulation/
    │   │   ├── AdvancedFireAlgorithm.java
    │   │   ├── FirePropagationAlgorithm.java
    │   │   ├── NaiveFireAlgorithm.java
    │   │   ├── PreventionFireAlgorithm.java
    │   │   ├── SimulationConfig.java
    │   │   └── SimulationEngine.java
    │   │
    │   └── main/
    │       ├── Main.java
    │       ├── Controller.java
    │       ├── ConsoleMain.java
    │       └── view.fxml
    │
    ├── docs/
    │   ├── index.html
    │   ├── model/
    │   ├── simulation/
    │   ├── display/
    │   └── main/
    │
    └── .gitignore
```

## Installation et lancement du projet

Ce projet contient deux modes d'exécution :

* une version **console**, utilisée pour tester le modèle de simulation sans interface graphique ;
* une version **graphique JavaFX**, utilisée pour lancer l'interface utilisateur.

Le projet doit pouvoir être compilé et lancé en ligne de commande.

---

## 1. Prérequis

Chaque machine doit avoir :

* **JDK 21** ou une version compatible ;
* **JavaFX SDK 21** pour lancer la version graphique ;
* un terminal : PowerShell sous Windows, ou Bash sous Linux/macOS.

Attention : il faut installer un **JDK**, pas seulement un JRE.
Le JDK contient `javac`, le compilateur Java.

---

## 2. Vérifier Java

Dans un terminal, taper :

```bash
java -version
javac -version
```

Les deux commandes doivent afficher une version récente de Java, par exemple :

```text
openjdk version "21..."
javac 21...
```

Si `javac` n'est pas reconnu, cela signifie que le JDK n'est pas installé ou que son dossier `bin` n'est pas dans le PATH.

---

## 3. Installer JavaFX

Télécharger le **JavaFX SDK** depuis :

```text
https://gluonhq.com/products/javafx/
```

Choisir :

```text
JavaFX Windows SDK
```

ou la version correspondant à votre système.

Après extraction, repérer le dossier `lib` du SDK JavaFX.

Exemples :

```text
C:\Users\NOM_UTILISATEUR\Desktop\javafx-sdk-21.0.11\lib
```

ou :

```text
/home/user/javafx-sdk-21.0.11/lib
```

Ce dossier doit contenir des fichiers comme :

```text
javafx.controls.jar
javafx.fxml.jar
javafx.graphics.jar
javafx.base.jar
```

---

## 4. Se placer dans le dossier du projet

Depuis la racine du dépôt Git :

```bash
cd propagation-feu
```

Le dossier doit contenir le dossier `src`.

---

# Lancement sous Windows PowerShell

## 5. Compiler et lancer la version console

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out

$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }

javac -d out $files

java -cp out Main
```

---

## 6. Compiler et lancer la version graphique JavaFX

Adapter la variable `$javafx` avec le chemin du dossier `lib` JavaFX sur votre machine.

Exemple :

```powershell
$javafx = "C:\Users\NOM_UTILISATEUR\Desktop\javafx-sdk-21.0.11\lib"
```

Puis lancer :

```powershell
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force out

$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }

javac --module-path $javafx --add-modules javafx.controls,javafx.fxml -d out $files

Copy-Item "src\main\view.fxml" "out\main\view.fxml" -Force

java --module-path $javafx --add-modules javafx.controls,javafx.fxml -cp out main.Main
```

---

# Lancement sous Linux / macOS / Git Bash

## 7. Compiler et lancer la version console

```bash
rm -rf out
mkdir -p out

javac -d out $(find src -name "*.java")

java -cp out Main
```

---

## 8. Compiler et lancer la version graphique JavaFX

Adapter la variable `JAVAFX_PATH` avec le chemin du dossier `lib` JavaFX sur votre machine.

Exemple :

```bash
JAVAFX_PATH="/home/user/javafx-sdk-21.0.11/lib"
```

Puis lancer :

```bash
rm -rf out
mkdir -p out

javac --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -d out $(find src -name "*.java")

mkdir -p out/main
cp src/main/view.fxml out/main/view.fxml

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp out main.Main
```

---

## 9. Problèmes fréquents

### `javac` n'est pas reconnu

Le JDK n'est pas installé ou n'est pas dans le PATH.

Vérifier :

```bash
java -version
javac -version
```

Il faut installer un JDK, par exemple JDK 21.

---

### `module not found: javafx.controls`

Le chemin vers JavaFX est incorrect.

Vérifier que le dossier indiqué contient bien :

```text
javafx.controls.jar
javafx.fxml.jar
```

Sous PowerShell :

```powershell
Test-Path $javafx
Get-ChildItem $javafx
```

---

### `Location is not set`

Le fichier FXML n'a pas été trouvé au lancement.

Il faut copier :

```text
src/main/view.fxml
```

vers :

```text
out/main/view.fxml
```

Sous PowerShell :

```powershell
Copy-Item "src\main\view.fxml" "out\main\view.fxml" -Force
```

Sous Linux/macOS/Git Bash :

```bash
mkdir -p out/main
cp src/main/view.fxml out/main/view.fxml
```

---

## 10. Recompiler après modification

Après une modification du code, il faut relancer les commandes de compilation.

Pour éviter les problèmes, il est conseillé de supprimer le dossier `out`, de recompiler entièrement le projet, puis de relancer l'application.

## Scripts PowerShell

Deux scripts sont disponibles pour compiler et lancer rapidement le projet depuis le dossier `propagation-feu`.

### Version console

```powershell
.\run-cli.ps1
```

### Version graphique

```powershell
.\run-gui.ps1 -JavaFxPath "C:\...\openjfx-21.0.11_windows-x64_bin-sdk\javafx-sdk-21.0.11\lib"
```

Remplacer le chemin par le chemin ou se trouve javafx sur son pc.

Les scripts .ps1 sont destinés à Windows PowerShell.
Pour Linux/macOS/Git Bash, utiliser les commandes Bash ou les scripts .sh équivalents.


## 10. Javadoc
Pour accéder à la javadoc, il suffit d'ouvrir:
- le dossier Docs 
- Puis d'ouvrir index.html dans un navigateur
