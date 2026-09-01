# ToDo API

API REST multi-usuario para la gestión de categorías y tareas. Cada usuario administra su propio espacio de trabajo de forma aislada, con autenticación segura mediante JWT, activación y recuperación de cuenta por email y un rol de administración para la gestión de usuarios.

Proyecto backend-only desarrollado con Spring Boot. Diseñado como base sólida para un frontend desacoplado (configurado por defecto para `http://localhost:5173`).

## Descripción

ToDo API resuelve la necesidad de un gestor de tareas personal y seguro, donde los datos de cada usuario permanecen completamente aislados. A diferencia de un CRUD genérico, el sistema implementa un ciclo de vida de usuario completo — registro, activación por email con expiración, login con validación de estado, cierre de sesión con revocación de tokens y recuperación de contraseña — y un dominio de negocio con reglas de unicidad y pertenencia a nivel de base de datos.

El objetivo es ofrecer una base backend profesional, mantenible y testeable, lista para ser consumida por cualquier cliente HTTP.

## Cómo se construyó — Arquitectura y decisiones

El proyecto es una aplicación Spring Boot única organizada por dominio en paquetes (`modules/{auth,category,task,user,sendEmails}`), con separación por capas dentro de cada paquete.

### Modelo por dominio

Se optó por una organización por dominio en lugar de una arquitectura por capas genérica centralizada:

```
src/main/java/com/jr/todo/
  TodoApplication.java
  config/ dto/ enums/ exception/ util/
  modules/
    auth/       -> seguridad, JWT, activación y recovery
    user/       -> gestión de usuarios y administración
    category/   -> categorías por usuario
    task/       -> tareas por usuario
    sendEmails/ -> configuración y envío de correos
```

Cada paquete está separado en capas y contiene sus propias clases `controller`, `service`, `entity`, `repository` y `dto`. Esta separación facilita el mantenimiento y aísla la lógica de pertenencia por usuario sin pretender un monolito modular estricto: controladores exponen la API, servicios concentran la lógica de negocio, entidades modelan el dominio y repositorios gestionan la persistencia.

### Stack

- **Java 21 + Spring Boot 4.0.3 + Maven Wrapper 3.9.12** — Base moderna y estable. Se usa exclusivamente `./mvnw` para garantizar reproducibilidad.
- **Spring WebMVC** — API síncrona REST sobre el puerto `8081`.
- **Spring Data JPA + MySQL + Hibernate `MariaDBDialect` + `ddl-auto=update`** — Persistencia relacional con creación automática del esquema para agilizar el desarrollo inicial.
- **Spring Security + JJWT 0.12.3 (HS256)** — Seguridad stateless con tokens firmados en Base64.
- **Spring Validation** — Validación declarativa en DTOs.
- **Spring Mail + Thymeleaf** — Envío de emails transaccionales con plantillas HTML (`ActivationEmail.html`, `AccountRecovery.html`).
- **Lombok + DevTools + Jackson** — Reducción de boilerplate, recarga en desarrollo y serialización.

## Flujos principales

### Registro, activación y autenticación

```
POST /auth/register {username, email, password}
  -> valida unicidad con UserValidationHelper, strip(), BCrypt, role=USER, enabled=false
  -> genera AccountActivationToken (UUID, expiresAt +24h, used=false)
  -> envía email con {activationUrl: frontendUrl + "/activation?token=", backendActivationUrl: :8081/auth/activation?token=, expirationHours: 24}

GET /auth/activation?token=xxx
  -> valida existencia, !used, !expired -> user.enabled=true, token.used=true

POST /auth/resendemail {email}
  -> solo si !enabled; elimina token previo y reenvía

POST /auth/login {email, password}
  -> busca por email, verifica BCrypt, valida isEnabled -> JwtService.getToken(user) -> {token, email, role, expiresIn}

POST /auth/logout (Header: Bearer <token>)
  -> extrae jti y exp, persiste RevokedToken si exp > now
```

### Recuperación de contraseña

```
POST /recovery/request {email}
  -> elimina token previo, genera UUID +2h, envía email con {formUrl: frontendUrl + "/reset-password?token=", expirationHours: 2}

POST /recovery/api/reset-password?token=xxx {newPassword, repeatPassword}
  -> valida existencia, !used, !expired, coincidencia de contraseñas -> BCrypt, token.used=true
```

## Requisitos

- Java 21
- Maven Wrapper incluido (`./mvnw`, no requiere instalación global de Maven)
- MySQL 8 en `localhost:3306` con base de datos `to_do` (se crea automáticamente si no existe)
- Cuenta de Gmail con App Password para el envío de correos (opcional para desarrollo sin email)

## Puesta en marcha

La ruta del proyecto contiene un espacio (`proyecto ToDo`), entrecomillar siempre las rutas en la terminal.

```bash
# 1. Clonar y entrar al proyecto
git clone <url-del-repositorio>
cd "proyecto ToDo/todo"

# 2. Configurar propiedades (archivos gitignorados, no se commitean)
cp "src/main/resources/application.properties.example" "src/main/resources/application.properties"
cp "src/main/resources/email.properties.example" "src/main/resources/email.properties"

# Editar application.properties:
# spring.datasource.password=TU_PASSWORD
# jwt.secret=$(openssl rand -base64 32)
# app.frontend.url=http://localhost:5173

# Editar email.properties:
# email.username=tu_email@gmail.com
# email.password=tu_app_password_sin_espacios

# 3. Levantar la aplicación
./mvnw spring-boot:run
# Disponible en http://localhost:8081

# 4. Ejecutar tests
./mvnw test
```

`EmailConfig` carga `email.properties` mediante `@PropertySource("classpath:email.properties")` con `smtp.gmail.com:587`. Si el archivo falta, la aplicación no inicia. La configuración de VS Code en `.vscode/launch.json` espera un archivo `.env` en la raíz para variables de entorno.

## Configuración

| Propiedad | Descripción | Valor por defecto |
|---|---|---|
| `server.port` | Puerto del servidor | `8081` |
| `spring.datasource.url` | URL de MySQL | `jdbc:mysql://localhost:3306/to_do` |
| `spring.datasource.username` | Usuario MySQL | `root` |
| `spring.datasource.password` | Contraseña MySQL | `123456` (example) |
| `jwt.secret` | Clave Base64 para HS256 | Generar con `openssl rand -base64 32` |
| `jwt.expiration` | Expiración del JWT en ms | `86400000` (24h) |
| `app.frontend.url` | URL del frontend para links de email y CORS prod | `http://localhost:5173` |
| `app.env` | Entorno (`dev`/`prod`), controla CORS | `dev` |
| `email.username` | Gmail para envíos | — |
| `email.password` | App Password Gmail sin espacios | — |

En `prod`, CORS permite solo `app.frontend.url`; en `dev` permite `*` con `AllowCredentials=false` y `maxAge=3600`.

## Testing

Tests unitarios con Mockito (`@ExtendWith(MockitoExtension.class)`), sin necesidad de base de datos ni Testcontainers. Fixtures centralizados en `DataProvider*`.

```bash
./mvnw test                                          # todos
./mvnw -Dtest=AuthServiceTest test                   # clase
./mvnw -Dtest=AuthServiceTest#testLogin test         # método
./mvnw -Dtest="com.jr.todo.modules.auth.**" test     # paquete
```

Para tests de integración con base de datos real, configurar MySQL o Testcontainers manualmente (no incluido).

## Estructura del proyecto

```
todo/
├── src/
│   ├── main/
│   │   ├── java/com/jr/todo/
│   │   │   ├── TodoApplication.java
│   │   │   ├── config/
│   │   │   │   └── JacksonConfig.java
│   │   │   ├── dto/
│   │   │   ├── enums/
│   │   │   │   └── Role.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ApiError.java
│   │   │   ├── util/
│   │   │   │   ├── TextFormat.java
│   │   │   │   ├── UserSearchMethods.java
│   │   │   │   └── UserValidationHelper.java
│   │   │   └── modules/
│   │   │       ├── auth/
│   │   │       │   ├── config/
│   │   │       │   ├── controller/
│   │   │       │   ├── service/
│   │   │       │   ├── entity/
│   │   │       │   ├── repository/
│   │   │       │   └── helpers/
│   │   │       ├── user/
│   │   │       │   ├── controller/
│   │   │       │   ├── service/
│   │   │       │   ├── entity/
│   │   │       │   └── repository/
│   │   │       ├── category/
│   │   │       ├── task/
│   │   │       └── sendEmails/
│   │   └── resources/
│   │       ├── application.properties.example
│   │       ├── email.properties.example
│   │       └── templates/
│   │           ├── ActivationEmail.html
│   │           ├── RecoveryEmail.html
│   │           └── AccountRecovery.html
│   └── test/
│       └── java/com/jr/todo/
│           ├── DataProvider*.java
│           ├── modules/
│           │   └── **/service/*Test.java
│           └── utils/
└── pom.xml
```

Ver `AGENTS.md` para el detalle operativo orientado a agentes de desarrollo (comandos exactos, invariantes y gotchas).

## Convenciones relevantes

- Normalización de nombres con `TextFormat.nameFormat` para categorías y tareas; `strip()` en autenticación. Comparar sin normalizar rompe las validaciones de unicidad.
- Todo acceso a datos filtra por `userId` obtenido del `SecurityContext` (email). No se exponen datos de otros usuarios.
- `deadline` es nullable; si existe debe ser mayor o igual a `dateCreation`. El filtro por fecha espera formato `dd/MM/yyyy HH:mm`.
- Lombok requiere `annotationProcessorPath` configurado y procesamiento de anotaciones habilitado en el IDE.
