#!/usr/bin/env bash
# Compila todo el proyecto a la carpeta out/
set -e
rm -rf out
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java")
echo "Compilación correcta. Ejecute:  java -cp out Main"
