# Despliegue gratuito en Render

Esta guia deja la API publicada en una URL `https://....onrender.com` usando el
plan Free de Render y una base de datos MySQL compatible en TiDB Cloud Starter.

## Requisitos

- Cuenta de GitHub.
- Cuenta de Render (se crea iniciando sesion con GitHub).
- Instancia **TiDB Cloud Starter** (MySQL compatible) creada.
- Datos de conexion de TiDB: host, puerto `4000`, usuario (`xxxxxxxx.root`) y contrasena.

## 1. Compilar el WAR

En NetBeans:

1. Clic derecho sobre el proyecto.
2. **Clean and Build**.
3. Verifica que se actualizo `dist/API_HealthBlood_512.war`.

## 2. Subir el proyecto a GitHub

Crea un repositorio vacio en GitHub (sin README, sin .gitignore) llamado
`API_HealthBlood_512` y luego ejecuta en la carpeta del proyecto:

```bash
git init -b main
git add .
git commit -m "API lista para desplegar en Render"
git remote add origin https://github.com/TU_USUARIO/API_HealthBlood_512.git
git push -u origin main
```

## 3. Crear el Web Service en Render

1. Entra a <https://dashboard.render.com>.
2. **New -> Web Service**.
3. Conecta tu cuenta de GitHub y selecciona el repositorio.
4. Configura:

| Campo | Valor |
| --- | --- |
| Name | `gluco-api-mtz-lab` |
| Region | `Virginia` (si tu TiDB esta en `us-east-1`) |
| Branch | `main` |
| Language | `Docker` |
| Dockerfile Path | `./Dockerfile` |
| Health Check Path | `/api/glucosa/saludar` |
| Plan | `Free` |

5. En **Environment Variables** agrega:

| Variable | Valor |
| --- | --- |
| `DB_HOST` | host de TiDB |
| `DB_PORT` | `4000` |
| `DB_NAME` | `gluco_api` |
| `DB_USER` | usuario completo de TiDB (`xxxxxxxx.root`) |
| `DB_PASSWORD` | contrasena de TiDB |
| `DB_SSL` | `true` |

> No configures `PORT`: el `Dockerfile` ya deja Tomcat en `10000`, que es el
> puerto por defecto de Render.

6. Clic en **Create Web Service** y espera el build.

## 4. Probar

Reemplaza `TU-APP` por el nombre que le diste en Render:

- Pagina web: `https://TU-APP.onrender.com/`
- Salud: `https://TU-APP.onrender.com/api/glucosa/saludar`
- Usuarios: `https://TU-APP.onrender.com/api/glucosa/getAllUsuarios`
- Medidas: `https://TU-APP.onrender.com/api/glucosa/getAllMedidas`
- Diagnostico de la base de datos: `https://TU-APP.onrender.com/api/glucosa/dbDiagnostico`

El endpoint `dbDiagnostico` indica si las variables `DB_HOST`, `DB_USER` y
`DB_PASSWORD` llegaron al contenedor (no muestra la contrasena), a que host y
base de datos intenta conectarse, y si las tablas `usuario` y `medida` existen.
Es la forma mas rapida de saber por que falla la conexion.

## 5. Actualizar la aplicacion

Cada vez que cambies el codigo:

1. **Clean and Build** en NetBeans.
2. `git add .`
3. `git commit -m "descripcion del cambio"`
4. `git push`

Render detecta el push y vuelve a desplegar automaticamente.

## Notas del plan gratuito

- El servicio **se suspende tras 15 minutos sin visitas**. La primera peticion
  puede tardar alrededor de 1 minuto mientras vuelve a arrancar.
- Memoria: 512 MB (el `Dockerfile` limita la JVM a 256 MB).
- El sistema de archivos es efimero: los datos viven en TiDB, no en Render.
- Nunca subas `DB_PASSWORD` al repositorio. Va solo en las variables de Render.
