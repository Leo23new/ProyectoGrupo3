@echo off
REM Compila y ejecuta la implementacion de REFERENCIA (carpeta local referencia_src\, no se sube a Git).
REM Uso: ejecutar_referencia.bat [--demo]
if not exist referencia_src (
  echo No existe referencia_src\. Copiela desde el zip a la raiz del proyecto.
  exit /b 1
)
if exist out_ref rmdir /s /q out_ref
mkdir out_ref
dir /s /b referencia_src\*.java > fuentes_ref.txt
javac -encoding UTF-8 -d out_ref @fuentes_ref.txt
del fuentes_ref.txt
java -Dstdout.encoding=UTF-8 -cp out_ref Main %*
