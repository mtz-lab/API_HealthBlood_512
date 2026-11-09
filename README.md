# glucoAPI - API HealthBlood 512

API REST para registrar y consultar mediciones de glucosa de los usuarios.

## Tecnologias

- Java 17
- Jakarta EE 10 (JAX-RS / Jersey 4)
- Apache Tomcat 10.1
- MySQL 8 / TiDB Cloud (MySQL compatible)
- Gson
- NetBeans (proyecto Ant)

## Estructura

```
src/java/org/lasalle/controller   Conexion y acceso a datos
src/java/org/lasalle/model        Modelos Usuario y Medida
src/java/org/lasalle/rest         Endpoints REST (RestGlucosa)
web/                              Pagina web y WEB-INF
database/                         Esquema y datos de ejemplo
Dockerfile                        Imagen para desplegar en Render
DEPLOY_RENDER.md                  Guia de despliegue gratuito
```

## Endpoints

Base: `/api/glucosa`

| Metodo | Ruta | Descripcion |
| --- | --- | --- |
| GET | `/saludar` | Prueba de vida |
| GET | `/saludarPersonalizado?nombre=Ana` | Saludo personalizado |
| GET | `/cuadrado?numero=5` | Calcula el cuadrado |
| GET | `/getAllUsuarios` | Lista de usuarios |
| GET | `/getUsuarioById?idUsuario=1` | Usuario por ID |
| GET | `/getAllMedidas` | Lista de mediciones |
| GET | `/getMedidasByUsuarioId?idUsuario=1` | Mediciones de un usuario |
| POST | `/insertUsuario` | Crea un usuario |
| POST | `/insertMedida` | Registra una medicion |

Los `POST` reciben `application/x-www-form-urlencoded`.

### Ejemplo de `insertUsuario`

```bash
curl -X POST https://TU-APP.onrender.com/api/glucosa/insertUsuario \
  -d "nombre=Ana" \
  -d "primerApellido=Garcia" \
  -d "segundoApellido=Lopez" \
  -d "email=ana@example.com"
```

### Ejemplo de `insertMedida`

```bash
curl -X POST https://TU-APP.onrender.com/api/glucosa/insertMedida \
  -d "idUsuario=1" \
  -d "glucosa=110" \
  -d "contexto=Ayunas" \
  -d "notas=Medicion de la manana"
```

## Ejecutar en local

1. Crea la base de datos ejecutando `database/schema.sql` en MySQL Workbench.
2. Opcionalmente ejecuta `database/sample_data.sql`.
3. Abre el proyecto en NetBeans y ejecuta con Tomcat 10.1.
4. La web queda en `http://localhost:8080/API_HealthBlood_512/`.

La conexion usa estas variables de entorno si existen:

| Variable | Valor local por defecto |
| --- | --- |
| `DB_HOST` | `127.0.0.1` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `gluco_api` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `root` |
| `DB_SSL` | `false` |

## Despliegue

Consulta [DEPLOY_RENDER.md](DEPLOY_RENDER.md) para publicar la API gratis en
Render usando una base de datos TiDB Cloud Starter.
