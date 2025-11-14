# Documentación Swagger - Volve a Casa API

## 📚 Descripción

Este proyecto incluye documentación automática de la API REST mediante **SpringDoc OpenAPI 3** (Swagger), que proporciona una interfaz interactiva para explorar y probar todos los endpoints de la aplicación.

## 🚀 Acceso a Swagger UI

### 1. Iniciar la aplicación

```bash
# Con Maven
mvn spring-boot:run

# O con Maven Wrapper
./mvnw spring-boot:run
```

### 2. Abrir Swagger UI en el navegador

Una vez iniciada la aplicación, accede a:

```
http://localhost:8080/swagger-ui.html
```

### 3. Ver la especificación OpenAPI en JSON

```
http://localhost:8080/api-docs
```

## 📋 Funcionalidades de Swagger UI

### Exploración de Endpoints

La interfaz de Swagger organiza los endpoints en las siguientes categorías:

- **🔐 Autenticación**: Login y manejo de tokens
- **👥 Usuarios**: CRUD de usuarios
- **🐾 Mascotas**: Gestión de mascotas perdidas y encontradas
- **👁️ Avistamientos**: Registro y consulta de avistamientos

### Probar Endpoints Directamente

1. Selecciona un endpoint (por ejemplo: `GET /users`)
2. Haz clic en **"Try it out"**
3. Completa los parámetros requeridos
4. Haz clic en **"Execute"**
5. Observa la respuesta en tiempo real

### Autenticación con Token

Para endpoints que requieren autenticación:

1. Primero, autentícate usando `POST /auth` con email y password en los headers
2. Copia el token del header de respuesta
3. En otros endpoints protegidos, pega el token en el campo `token` del header

**Formato del token**: `{userId}123456` (ejemplo: `1123456`)

## 🧪 Casos de Test Documentados

Cada endpoint incluye referencias a sus casos de test unitarios correspondientes. Por ejemplo:

### UserController Tests

- `UserControllerTest.listAllUsersOrderByName_whenEmpty_returnsNoContent()`
- `UserControllerTest.listAllUsersOrderByName_whenUsersExist_returnsOkAndList()`
- `UserControllerTest.createUser_whenUserDoesNotExist_returnsCreated()`
- `UserControllerTest.createUser_whenUserExists_returnsConflict()`
- `UserControllerTest.getUserById_whenUserExistsAndTokenValid_returnsOk()`
- `UserControllerTest.getUserById_whenTokenInvalid_returnsUnauthorized()`
- `UserControllerTest.getUserById_whenUserDoesNotExist_returnsNotFound()`
- `UserControllerTest.updateUser_whenTokenValid_returnsOk()`

### PetController Tests

- `PetControllerTest.listAllLostPets_whenEmpty_returnsNoContent()`
- `PetControllerTest.listAllLostPets_whenPetsExist_returnsOkAndList()`
- `PetControllerTest.getPetById_whenPetExists_returnsOk()`
- `PetControllerTest.getPetById_whenPetDoesNotExist_returnsNotFound()`
- `PetControllerTest.listAllPets_whenEmpty_returnsNoContent()`
- `PetControllerTest.listAllPets_whenPetsExist_returnsOkAndList()`

### SightingController Tests

- `SightingControllerTest.listAllSightings_whenEmpty_returnsNoContent()`
- `SightingControllerTest.listAllSightings_whenSightingsExist_returnsOkAndList()`
- `SightingControllerTest.getSightingsByPetId_whenPetDoesNotExist_returnsNotFound()`
- `SightingControllerTest.getSightingsByPetId_whenPetExistsButNoSightings_returnsNoContent()`
- `SightingControllerTest.getSightingsByPetId_whenSightingsExist_returnsOkAndList()`
- `SightingControllerTest.createSighting_whenTokenDoesNotEndWith123456_returnsUnauthorized()`
- `SightingControllerTest.createSighting_whenUserDoesNotExist_returnsUnauthorized()`
- `SightingControllerTest.createSighting_whenDataInvalid_returnsBadRequest()`
- `SightingControllerTest.createSighting_whenUserNotFound_returnsNotFound()`
- `SightingControllerTest.createSighting_whenPetNotFound_returnsNotFound()`
- `SightingControllerTest.createSighting_whenValidData_returnsCreated()`
- `SightingControllerTest.getSightingById_whenSightingDoesNotExist_returnsNotFound()`
- `SightingControllerTest.getSightingById_whenSightingExists_returnsOk()`

## 📝 Ejemplos de Uso

### Ejemplo 1: Listar todas las mascotas perdidas

```http
GET http://localhost:8080/pets/lost
Accept: application/json
```

**Respuesta esperada**: `200 OK` con array de mascotas o `204 No Content`

### Ejemplo 2: Crear un usuario

```http
POST http://localhost:8080/users
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "password123",
  "name": "Juan",
  "lastName": "Pérez",
  "phone": "11 1234-5678",
  "city": "La Plata",
  "neighborhood": "Centro",
  "latitude": -34.6037,
  "longitude": -58.3816
}
```

**Respuesta esperada**: `201 Created` o `409 Conflict` si el email ya existe

### Ejemplo 3: Autenticación

```http
POST http://localhost:8080/auth
email: usuario@example.com
password: password123
```

**Respuesta esperada**: `200 OK` con token en el header `token`

### Ejemplo 4: Crear un avistamiento (requiere token)

```http
POST http://localhost:8080/sightings
Content-Type: application/json
token: 1123456

{
  "petId": 1,
  "latitude": -34.6037,
  "longitude": -58.3816,
  "photoBase64": "base64_encoded_image",
  "date": "2025-11-13",
  "comment": "Vi la mascota en el parque"
}
```

**Respuesta esperada**: `201 Created` con los datos del avistamiento

## ⚙️ Configuración

La configuración de SpringDoc está definida en:

### `application.properties`

```properties
# SpringDoc OpenAPI (Swagger) Configuration
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.show-actuator=false
```

### `OpenApiConfig.java`

Clase de configuración que define:
- Información general de la API
- Versión
- Descripción
- Contacto
- Servidores disponibles

## 🔧 Dependencias

### Maven (`pom.xml`)

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.6.0</version>
</dependency>
```

## 📖 Recursos Adicionales

- [Documentación oficial de SpringDoc](https://springdoc.org/)
- [Especificación OpenAPI 3](https://swagger.io/specification/)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

## 💡 Ventajas de usar Swagger

1. **Documentación automática**: Se genera desde el código
2. **Siempre actualizada**: Refleja el estado actual de la API
3. **Interactiva**: Permite probar endpoints sin herramientas externas
4. **Validación**: Muestra esquemas de datos y validaciones
5. **Ejemplos**: Incluye ejemplos de requests y responses
6. **Testing**: Facilita el testing manual de la API

## 🎯 Mejores Prácticas Implementadas

- ✅ Todas las operaciones están documentadas con `@Operation`
- ✅ Los parámetros tienen descripciones claras con `@Parameter`
- ✅ Las respuestas están documentadas con `@ApiResponses`
- ✅ Los esquemas de datos están definidos con `@Schema`
- ✅ Se incluyen ejemplos para facilitar el uso
- ✅ Referencias a casos de test para cada endpoint
- ✅ Organización por tags (categorías)
- ✅ Descripción detallada de códigos de estado HTTP

---

**Proyecto**: Volve a Casa - Sistema de Mascotas Perdidas  
**Curso**: TTPS Java  
**Grupo**: 01
