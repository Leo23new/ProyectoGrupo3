# Guía de Git y GitHub para el Grupo 3

Repositorio: https://github.com/Leo23new/ProyectoGrupo3

## Una sola vez (cada integrante)

```bash
git config --global user.name  "Su Nombre Real"
git config --global user.email "el-correo-de-su-cuenta-de-GitHub"   # debe ser el mismo que usa en GitHub
git clone https://github.com/Leo23new/ProyectoGrupo3.git
cd ProyectoGrupo3
```

> Importante: el correo de `git config` debe coincidir con el de su cuenta de GitHub; si no, sus commits no aparecen a su nombre.
> El dueño del repositorio (Leo23new) debe **invitar a los 5 integrantes como colaboradores**: *Settings → Collaborators → Add people*.

## Subida inicial del esqueleto (solo Edwin, una vez)

Copiar el contenido de este proyecto dentro de la carpeta clonada y:

```bash
git add .
git commit -m "Agrega esqueleto inicial: README, dataset, scripts y documentación"
git push origin main
```

## Flujo diario de cada integrante

```bash
git checkout main
git pull                                   # siempre actualizar antes de empezar
git checkout -b feature/nombre-de-su-rama  # solo la primera vez; luego: git checkout feature/...

# ...trabajar y probar...
./compilar.sh && java -cp out pruebas.Pruebas

git add src/estructuras/Cola.java          # agregar archivos concretos, no "git add ." a ciegas
git commit -m "Agrega Cola genérica con nodos enlazados"
git push -u origin feature/nombre-de-su-rama
```

Luego, en GitHub: **Compare & pull request** → describir qué hace → pedir revisión a un compañero → **Merge**.

## Reglas del grupo

1. **Nadie hace push directo a `main`.** Todo entra por Pull Request.
2. **Commits pequeños y con mensaje claro** en infinitivo o presente ("Agrega…", "Corrige…", "Implementa…"). Evitar "cambios" o "listo".
3. **Un archivo, un dueño:** si necesitan cambiar el archivo de otro, avisen o abran un Pull Request y pídanle revisión.
4. **Antes de hacer push:** que compile y que `pruebas.Pruebas` pase.
5. **Cada integrante debe tener commits propios** repartidos en varios días. El docente revisa el historial.
6. **No se suben** archivos `.class`, la carpeta `out/` ni credenciales. **La API key nunca va en el código ni en GitHub**: solo en la variable de entorno.

## Si hay conflictos al hacer merge

```bash
git checkout main && git pull
git checkout feature/mi-rama
git merge main            # Git marca los conflictos con <<<<<<< ======= >>>>>>>
# editar el archivo, dejar la versión correcta, y:
git add archivo-con-conflicto
git commit
git push
```

## Comandos útiles

| Para… | Comando |
|---|---|
| Ver el estado | `git status` |
| Ver historial corto | `git log --oneline --graph --all` |
| Ver qué cambié | `git diff` |
| Deshacer cambios sin commit de un archivo | `git restore archivo` |
| Cambiar de rama | `git checkout nombre-rama` |
