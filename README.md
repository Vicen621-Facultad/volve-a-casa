# Volvé a Casa 🐾

Sistema web integral para la gestión de mascotas perdidas y encontradas. Permite a usuarios reportar avistamientos de mascotas, gestionar sus perfiles y utilizar tecnología de geolocalización e inteligencia artificial para ayudar a reunir mascotas con sus dueños. Incluye notificaciones automáticas vía Telegram.

## 👥 Colaboradores

- [Maria Luisa Britez](https://github.com/britezlmaria)
- [Vicente Garcia Marti](https://github.com/vicen621)
- [Matias Guaymas](https://github.com/MatiasGuaymas)
- [Matheo Lamiral](https://github.com/MatheoLamiral)

## 💻 Lenguajes de Programación

### Lenguajes Principales
- **Java** 17+ (Backend - Spring Boot, JPA/Hibernate)
- **TypeScript** (Frontend - Angular 21)
- **SQL** (PostgreSQL 16 con PostGIS - Consultas geoespaciales)

### Lenguajes de Marcado y Estilos
- **HTML5** (Templates Angular)
- **CSS3** (Tailwind CSS 4)

### Lenguajes de Configuración
- **YAML** (Docker Compose)
- **JSON** (Configuración Maven y npm)
- **Properties** (Configuración Spring Boot)

## 🚀 Tecnologías Utilizadas

### Backend
- **Framework:** Spring Boot 3.5.7
- **ORM:** Spring Data JPA / Hibernate
- **Base de Datos:** PostgreSQL 16 con PostGIS 3.4
- **Autenticación:** JWT (JSON Web Tokens) con jjwt
- **Validación:** Spring Boot Starter Validation
- **Geolocalización:** PostGIS (consultas espaciales)
- **Email:** Spring Boot Starter Mail
- **Bot de Telegram:** Telegram Bot API
- **IA:** GROQ API
- **Documentación API:** SpringDoc OpenAPI (Swagger)
- **Containerización:** Docker

### Frontend
- **Framework:** Angular 21
- **Gestión de Estado:** RxJS
- **HTTP Client:** Angular HttpClient
- **Estilos:** Tailwind CSS 4
- **Componentes UI:** Flowbite 4
- **Mapas:** Leaflet con ngx-leaflet
- **Alertas:** SweetAlert2
- **Build Tool:** Angular CLI

### Herramientas de Desarrollo
- **Gestión de Dependencias Backend:** Maven
- **Gestión de Dependencias Frontend:** npm
- **Control de Versiones:** Git
- **IDE Recomendado:** Visual Studio Code, IntelliJ IDEA
- **Administración DB:** pgAdmin 4
- **Testing:** Vitest (Frontend), Mockito (Backend)

## 📋 Requisitos Previos

- **Java:** JDK 17 o superior
- **Node.js:** 18.x o superior
- **Maven:** 3.8+ (o usar mvnw incluido)
- **Docker:** 20.x o superior
- **Docker Compose:** 2.x o superior
- **Git:** 2.x o superior

## 📦 Instalación

### 1. Clonar el Repositorio

```bash
git clone https://github.com/MatiasGuaymas/TTPS
cd volve-a-casa
```

### 2. Configuración de Variables de Entorno

Crear el archivo `.env` en la raíz del proyecto con el siguiente contenido:

```env
# Configuración de PostgreSQL
POSTGRES_USER=admin
POSTGRES_PASSWORD=admin
POSTGRES_DB=grupo01

# Configuración de pgAdmin
PGADMIN_EMAIL=test@gmail.com
PGADMIN_PASSWORD=admin

# Configuración de Email (Gmail SMTP)
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=tu_contraseña_de_aplicacion

# Bot de Telegram
TELEGRAM_BOT_TOKEN=tu-telegram-bot-token

# APIs de IA
GROQ_API_KEY=tu-groq-api-key
OPENAI_KEY=tu-openai-api-key
```

## 🔧 Ejecución del Proyecto

### Backend (Spring Boot)

#### 1. Levantar servicios de infraestructura (PostgreSQL, pgAdmin):
```bash
docker-compose up -d
```

#### 2. Navegar al directorio del backend:
```bash
cd backend
```

#### 3. Compilar el proyecto con Maven:
```bash
# En Linux/macOS
./mvnw clean install

# En Windows
mvnw.cmd clean install
```

#### 4. Ejecutar la aplicación:
```bash
# En Linux/macOS
./mvnw spring-boot:run

# En Windows
mvnw.cmd spring-boot:run
```

El backend estará disponible en: **http://localhost:8080**

La documentación Swagger UI estará en: **http://localhost:8080/swagger-ui.html**

### Frontend (Angular)

#### 1. Navegar al directorio del frontend:
```bash
cd frontend
```

#### 2. Instalar dependencias con npm:
```bash
npm install
```

#### 3. Ejecutar el servidor de desarrollo:
```bash
npm start
```

El frontend estará disponible en: **http://localhost:4200**

## 🐳 Servicios Docker

Una vez ejecutado `docker-compose up -d`, los siguientes servicios estarán disponibles:

| Servicio | Puerto | Acceso | Credenciales |
|----------|--------|--------|--------------|
| PostgreSQL | 5433 | localhost:5433 | User: admin<br>Pass: admin<br>DB: grupo01 |
| pgAdmin | 5050 | http://localhost:5050 | Email: test@gmail.com<br>Pass: admin |

### Conectar pgAdmin a PostgreSQL

1. Acceder a http://localhost:5050
2. Login con las credenciales de pgAdmin
3. Agregar nuevo servidor:
   - **Name:** volve-a-casa
   - **Host:** db (nombre del servicio en Docker)
   - **Port:** 5432 (puerto interno del contenedor)
   - **Username:** admin
   - **Password:** admin
   - **Database:** grupo01

## 📁 Estructura del Proyecto

```
volve-a-casa/
├── backend/                              # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/io/github/grupo01/volve_a_casa/
│   │   │   │   ├── config/               # Configuraciones Spring (Security, Telegram, etc.)
│   │   │   │   ├── controllers/          # REST Controllers
│   │   │   │   │   └── dto/              # Data Transfer Objects
│   │   │   │   ├── exceptions/           # Manejo global de excepciones
│   │   │   │   ├── filters/              # Filtros de seguridad JWT
│   │   │   │   ├── integrations/         # Cliente de IA (GROQ)
│   │   │   │   ├── persistence/          # Capa de persistencia
│   │   │   │   │   ├── entities/         # Entidades JPA (User, Pet, Sighting, etc.)
│   │   │   │   │   ├── repositories/     # Repositorios Spring Data
│   │   │   │   │   └── filters/          # Filtros de búsqueda
│   │   │   │   ├── security/             # Autenticación y autorización
│   │   │   │   ├── services/             # Lógica de negocio
│   │   │   │   └── telegram/             # Bot de Telegram
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── telegram-messages.properties
│   │   │       └── static/
│   │   └── test/                         # Tests unitarios (JUnit + Mockito)
│   │       └── java/
│   │           ├── controllers/
│   │           ├── services/
│   │           └── filters/
│   └── pom.xml                           # Configuración Maven
├── frontend/                             # Frontend Angular
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/                     # Módulos core
│   │   │   │   ├── guards/               # Route guards (Auth, Admin)
│   │   │   │   ├── interceptors/         # HTTP interceptors (JWT)
│   │   │   │   ├── models/               # Modelos TypeScript
│   │   │   │   └── services/             # Servicios de datos
│   │   │   ├── features/                 # Módulos de funcionalidades
│   │   │   │   ├── admin/                # Panel de administración
│   │   │   │   ├── auth/                 # Login y registro
│   │   │   │   ├── home/                 # Página principal
│   │   │   │   ├── mascota/              # Gestión de mascotas
│   │   │   │   ├── profile/              # Perfil de usuario
│   │   │   │   └── ranking/              # Ranking de usuarios
│   │   │   ├── layout/                   # Componentes de layout (navbar, footer)
│   │   │   └── shared/                   # Componentes compartidos
│   │   │       └── components/           # Carousel, map, list, userCard
│   │   ├── assets/                       # Imágenes y recursos estáticos
│   │   └── styles.css                    # Estilos globales
│   ├── angular.json                      # Configuración Angular
│   ├── package.json                      # Dependencias npm
│   └── tsconfig.json                     # Configuración TypeScript
├── docker-compose.yml                    # Orquestación de servicios
├── .env                                  # Variables de entorno
└── README.md
```

## 👤 Usuario por Defecto

Al iniciar la aplicación por primera vez, se crea automáticamente un usuario administrador:

| Email | Password | Rol |
|-------|----------|-----|
| admin@volveacasa.com | admin123 | Administrador |

Este usuario se crea a través del componente `DataInitializer` y tiene todos los privilegios del sistema.

## 🎯 Funcionalidades Principales

### Gestión de Mascotas
- ✅ Registro de mascotas perdidas y encontradas
- ✅ Carga de fotos y descripción detallada
- ✅ Búsqueda por características (raza, tamaño, color)

### Sistema de Avistamientos
- ✅ Reportar avistamientos con geolocalización
- ✅ Visualización en mapa interactivo con Leaflet
- ✅ Consultas espaciales con PostGIS
- ✅ Filtrado por rango de fechas

### Notificaciones Inteligentes
- ✅ Bot de Telegram para alertas en tiempo real
- ✅ Notificaciones por email con Spring Mail
- ✅ Alertas automáticas de coincidencias
- ✅ Sistema de matching entre mascotas y avistamientos

### Gestión de Usuarios
- ✅ Registro y autenticación con JWT
- ✅ Perfiles personalizables
- ✅ Ranking de usuarios por colaboración
- ✅ Panel de administración
- ✅ Guards y protección de rutas

## 🔐 Seguridad

- Autenticación basada en JWT (JSON Web Tokens)
- Contraseñas hasheadas con BCrypt
- Interceptores JWT en Angular para peticiones autenticadas
- Guards para protección de rutas (AuthGuard, AdminGuard)
- CORS configurado para orígenes autorizados
- Validación de datos con Spring Validation
- Sanitización de inputs en formularios Angular

## 🛠️ Comandos Útiles

### Backend
```bash
# Compilar proyecto
./mvnw clean install

# Ejecutar aplicación
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test

# Generar WAR para producción
./mvnw clean package

# Ver documentación API
# Acceder a http://localhost:8080/swagger-ui.html
```

### Frontend
```bash
# Instalar dependencias
npm install

# Ejecutar en desarrollo con proxy
npm start

# Compilar para producción
npm run build

# Ejecutar tests
npm test

# Modo watch (desarrollo)
npm run watch
```

### Docker
```bash
# Levantar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Ver logs
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f db

# Reiniciar servicios
docker-compose restart

# Ver estado de contenedores
docker-compose ps

# Eliminar volúmenes (⚠️ borra datos)
docker-compose down -v
```

## 📝 Notas Adicionales

### Configuración del Bot de Telegram
1. Crear un bot con [@BotFather](https://t.me/botfather) en Telegram
2. Obtener el token del bot
3. Configurar el token en el archivo `.env`
4. El bot enviará notificaciones automáticas de avistamientos

### Configuración de Email (Gmail)
1. Habilitar "Acceso de aplicaciones menos seguras" o usar "Contraseñas de aplicación"
2. Configurar `MAIL_USERNAME` y `MAIL_PASSWORD` en `.env`
3. El sistema enviará emails de verificación y notificaciones

### APIs de IA
- **GROQ API:** Para análisis con IA

### PostGIS (Geolocalización)
- PostGIS se instala automáticamente con la imagen `postgis/postgis`
- Permite consultas espaciales para búsqueda por zona
- Cálculo de distancias entre avistamientos y ubicación de mascotas

### Proxy de Desarrollo
El frontend usa un proxy configurado en `proxy.conf.json` para evitar problemas de CORS en desarrollo:
```json
{
  "/api": {
    "target": "http://localhost:8080",
    "secure": false
  }
}
```

## 🐛 Solución de Problemas

### Error de conexión a PostgreSQL
```bash
# Verificar que el contenedor esté corriendo
docker ps

# Reiniciar el contenedor
docker-compose restart db

# Ver logs del contenedor
docker-compose logs db
```

### Error "Port already in use"
```bash
# Backend (puerto 8080)
lsof -i :8080
kill -9 <PID>

# Frontend (puerto 4200)
lsof -i :4200
kill -9 <PID>
```

### Error de compilación Backend
```bash
# Limpiar caché de Maven
./mvnw clean

# Forzar actualización de dependencias
./mvnw clean install -U
```

### Error de instalación Frontend
```bash
# Eliminar node_modules y package-lock.json
rm -rf node_modules package-lock.json

# Reinstalar dependencias
npm install

# Si persiste, limpiar caché de npm
npm cache clean --force
npm install
```

### Error con PostGIS
```bash
# Verificar que PostGIS esté habilitado
docker exec -it postgres_container psql -U admin -d grupo01

# Dentro de psql
\dx

# Si no está habilitado:
CREATE EXTENSION postgis;
```

### Error de CORS en desarrollo
- Asegurarse de que el proxy esté configurado en `proxy.conf.json`
- Iniciar Angular con: `npm start` (usa el proxy automáticamente)
- Verificar configuración de CORS en el backend

### Bot de Telegram no envía mensajes
```bash
# Verificar que el token esté correcto en .env
# Verificar que el bot esté iniciado
# Revisar logs del backend para errores de Telegram API
```

## 📊 API Documentation

Una vez que el backend esté corriendo, la documentación completa de la API REST está disponible en:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs JSON:** http://localhost:8080/api-docs

La documentación incluye todos los endpoints, modelos de datos, códigos de respuesta y permite probar las peticiones directamente desde el navegador.

## 🧪 Testing

### Backend (JUnit + Mockito)
```bash
# Ejecutar todos los tests
./mvnw test

# Ejecutar tests con cobertura
./mvnw test jacoco:report

# Ejecutar un test específico
./mvnw test -Dtest=NombreDelTest
```

### Frontend (Vitest)
```bash
# Ejecutar tests
npm test

# Ejecutar tests en modo watch
npm test -- --watch

# Ejecutar tests con cobertura
npm test -- --coverage
```

## 📄 Licencia

Este proyecto es parte del curso de **Taller de Tecnologías de Producción de Software (Opción A - JAVA) 2025 - UNLP**.
