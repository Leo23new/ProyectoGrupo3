@echo off
REM Compila todo el proyecto a la carpeta out\
if exist out rmdir /s /q out
mkdir out
setlocal EnableDelayedExpansion
type nul > fuentes.txt
for /r src %%f in (*.java) do (
    set "fuente=%%f"
    echo "!fuente:\=/!" >> fuentes.txt
)
javac -encoding UTF-8 -d out @fuentes.txt
if errorlevel 1 exit /b 1
del fuentes.txt
echo Compilacion correcta. Ejecute:  java -Dstdout.encoding=UTF-8 -cp out Main
