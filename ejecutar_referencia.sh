#!/usr/bin/env bash
# Compila y ejecuta la implementacion de REFERENCIA (carpeta local referencia_src/, no se sube a Git).
# Sirve para ver como debe comportarse el sistema completo. Uso: ./ejecutar_referencia.sh [--demo]
set -e
[ -d referencia_src ] || { echo "No existe referencia_src/. Copiela desde el zip a la raiz del proyecto."; exit 1; }
rm -rf out_ref && mkdir -p out_ref
javac -encoding UTF-8 -d out_ref $(find referencia_src -name "*.java")
java -Dstdout.encoding=UTF-8 -cp out_ref Main "$@"
