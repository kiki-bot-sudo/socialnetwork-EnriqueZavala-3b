# Social Network — Backend

Backend de una red social orientada a fotografía: usuarios que publican fotos, comentan y dan like. Construido con Spring Boot siguiendo una arquitectura por capas orientada al dominio.

## Stack

`Java 27` · `Gradle 9.9` · `Spring Boot 4` · `Spring Security` · `Spring Data JPA` · `PostgreSQL` · `JWT (jjwt)` · `MapStruct` · `springdoc-openapi (Swagger)` · `JUnit`

## Arquitectura

El proyecto separa responsabilidades en tres capas independientes:

```
domain/          → modelos de negocio, interfaces de repositorio y servicios, excepciones
persistence/      → entidades JPA, repositorios concretos y mappers (dominio ↔ entidad)
web/               → controladores REST, DTOs y manejo global de errores
security/          → autenticación JWT y configuración de Spring Security
```

**Por qué así:** el dominio no conoce los detalles de JPA ni de HTTP — solo trabaja con sus propios modelos e interfaces. `persistence/` implementa esas interfaces contra PostgreSQL, y `web/` expone el dominio vía REST usando DTOs, nunca las entidades directamente. Esto significa que se podría cambiar de PostgreSQL a otra base de datos, o agregar un cliente distinto a REST, sin tocar la lógica de negocio.

Los `Mapper` (`UserMapper`, `PhotographMapper`, `CommentMapper`, `LikePhotographyMapper`) se encargan de convertir entre el modelo de dominio y las entidades de persistencia — esa conversión explícita evita que un cambio en la tabla de base de datos rompa el resto del sistema.

## Funcionalidad

- **Usuarios**: registro y autenticación.
- **Fotografías**: publicación de fotos por usuario, cada una con una **categoría** (Paisaje, Retrato, Urbano, Naturaleza, Animales, Arquitectura, Comida, Atardecer). `GET /api/photographs/categories` devuelve la lista válida.
- **Comentarios**: comentarios sobre fotografías.
- **Likes**: sistema de likes sobre fotografías.

## Seguridad

Autenticación stateless con **JWT**: `JwtUtil` genera y valida tokens, `JwtFilter` los intercepta en cada request, y `SecurityConfig` define qué rutas requieren autenticación. El secreto y el tiempo de expiración del token se configuran por variables de entorno (`JWT_SECRET`, `JWT_EXPIRATION_MS`), no quedan hardcodeados en el código.

**Login con Firebase** (`POST /auth/firebase`): la app Flutter inicia sesión con Firebase Authentication y envía su *ID token*. `FirebaseTokenVerifier` comprueba la firma con las llaves públicas de Google, el emisor y la audiencia (`app.firebase.project-id`) y entrega un JWT del backend; si el correo no tiene cuenta, la crea. La contraseña solo la conoce Firebase, así que la recuperación de contraseña de Firebase no desincroniza las cuentas.

**CORS**: solo se permiten orígenes locales (`http://localhost:*`, `http://127.0.0.1:*`) para la app Flutter web; se puede cambiar con `app.cors.allowed-origins`.

## Manejo de errores

`GlobalExceptionHandler` centraliza las respuestas de error (por ejemplo `ResourceNotFoundException`, `InvalidCredentialsException`), para que la API devuelva respuestas HTTP consistentes en vez de errores genéricos.

## Documentación de la API

La API está documentada con **OpenAPI/Swagger** (`OpenApiConfig`) — al levantar el proyecto, la documentación interactiva queda disponible automáticamente.

## Pruebas

Incluye pruebas unitarias para el servicio de autenticación y la utilidad de JWT (`AuthServiceTest`, `JwtUtilTest`).

## Cómo correrlo localmente

```bash
# Requiere JDK 27, PostgreSQL corriendo y las variables de entorno configuradas.
# Copia src/main/resources/application-dev.properties.example como
# application-dev.properties y pon tus datos de PostgreSQL.
export JWT_SECRET=tu-secreto-de-al-menos-32-caracteres
./gradlew bootRun
```

### Datos de prueba

Con el backend corriendo, en otra terminal de PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\seed-data.ps1
```

Crea 3 usuarios, 5 fotos, comentarios y likes usando la API (las contraseñas quedan cifradas con BCrypt). Se puede ejecutar varias veces sin duplicar usuarios ni likes.

Perfiles disponibles: `dev` (por defecto) y `pdn`, configurables en `application-*.properties`.
