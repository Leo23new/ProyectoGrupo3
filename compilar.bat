@echo off
REM Compila todo el proyecto a la carpeta out\
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > fuentes.txt
javac -encoding UTF-8 -d out @fuentes.txt
del fuentes.txt
echo Compilacion correcta. Ejecute:  java -Dstdout.encoding=UTF-8 -cp out Main
