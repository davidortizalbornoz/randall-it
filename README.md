# 🚀 Randall IT - API de Grafos

## 📋 Descripción

**Randall IT** es una API REST desarrollada en Java Spring Boot que implementa un sistema de cálculo de rutas más rápidas entre ubicaciones utilizando el algoritmo de Dijkstra. La aplicación permite cargar datos de grafos desde archivos CSV y calcular rutas óptimas en tiempo real.

## 🎯 Características Principales

- **Algoritmo de Dijkstra**: Implementación optimizada para encontrar rutas más rápidas
- **Carga de datos CSV**: Soporte para archivos con formato `loc_start;loc_end;time`
- **API REST**: Endpoints documentados con Swagger/OpenAPI
- **Bulk Upload**: Carga dinámica de archivos CSV para actualizar el grafo
- **Documentación interactiva**: Swagger UI integrado
- **Tests unitarios**: Cobertura completa de funcionalidades

## 🏗️ Arquitectura

### Tecnologías Utilizadas
- **Java 21**: Lenguaje de programación
- **Spring Boot 3.2.12**: Framework de desarrollo
- **Gradle**: Sistema de build
- **Swagger/OpenAPI**: Documentación de API
- **JUnit 5**: Framework de testing
- **Lombok**: Reducción de código boilerplate

### Estructura del Proyecto
```
randall_it/
├── 📖 README.md                     # Documentación principal
├── 🧪 TEST_DOCUMENTATION.md        # Documentación de tests
├── 🔗 API_EXAMPLES.md              # Ejemplos de uso
├── 🔧 build.gradle                 # Configuración de build
├── ⚙️ settings.gradle              # Configuración del proyecto
├── 🐳 Dockerfile                   # Configuración de contenedor
├── 🐳 docker-compose.yml           # Orquestación de servicios
├── 🐳 .dockerignore                # Archivos excluidos de Docker
├── 🐳 docker-build.sh              # Script de automatización
├── 🚫 .gitignore                   # Archivos ignorados por Git
└── src/
    ├── main/java/cl/randall/
    │   ├── RandallApp.java              # Clase principal de Spring Boot
    │   ├── config/
    │   │   └── OpenApiConfig.java       # Configuración de Swagger/OpenAPI
    │   ├── controllers/
    │   │   └── RandallController.java   # Controladores REST
    │   ├── models/
    │   │   ├── ConexionGrafo.java       # Modelo de conexión
    │   │   └── ResultadoRuta.java       # Modelo de resultado de ruta
    │   └── services/
    │       └── GrafoService.java        # Lógica de negocio y algoritmo de Dijkstra
    ├── main/resources/
    │   ├── application.properties       # Configuración de la aplicación
    │   ├── application-docker.properties # Configuración para Docker
    │   └── grafos_light.csv            # Archivo CSV de ejemplo
    └── test/                           # Tests unitarios
```

## 🚀 Instalación y Ejecución

### Prerrequisitos
- Java 21 o superior
- Gradle 8.6 o superior
- Docker (opcional, para containerización)

### Opción 1: Ejecución Local

1. **Clonar el repositorio**
   ```bash
   git clone <repository-url>
   cd randall_it
   ```

2. **Compilar el proyecto**
   ```bash
   gradle build
   ```

3. **Ejecutar la aplicación**
   ```bash
   gradle bootRun
   ```

4. **Acceder a la documentación**
   - **Swagger UI**: http://localhost:8080/swagger-ui/index.html
   - **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### Opción 2: Ejecución con Docker

#### **Usando Docker Compose (Recomendado)**
```bash
# Construir y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down
```

#### **Usando Script de Docker**
```bash
# Hacer ejecutable el script
chmod +x docker-build.sh

# Construir imagen
./docker-build.sh build

# Ejecutar contenedor
./docker-build.sh run

# Ver logs
./docker-build.sh logs

# Detener contenedor
./docker-build.sh stop
```

#### **Usando Docker directamente**
```bash
# Construir imagen
docker build -t randall-it:latest .

# Ejecutar contenedor
docker run -d -p 8080:8080 --name randall-it-api randall-it:latest

# Ver logs
docker logs -f randall-it-api
```

## 📊 Estructura de Datos

### Formato CSV
Los archivos CSV deben seguir el formato:
```csv
loc_start;loc_end;time
R11;R12;20
R12;R13;9
CP1;R11;84
```

**Campos:**
- `loc_start`: Ubicación de origen (String)
- `loc_end`: Ubicación de destino (String)
- `time`: Tiempo de viaje en minutos (Integer)

### Carga de Datos

#### **Carga Inicial (Arranque)**
Al iniciar la aplicación, se precargan automáticamente los datos mínimos del archivo `grafos_light.csv` que contiene **11 conexiones** entre **6 ubicaciones únicas**. Estos datos están disponibles inmediatamente para consultas y cálculos de rutas.

#### **Carga Dinámica (Reemplazo)**
La aplicación incluye el endpoint **`POST /bulk-upload`** que permite cargar dinámicamente una nueva estructura de grafos desde un archivo CSV, reemplazando completamente los datos precargados. Este endpoint acepta archivos en formato `multipart/form-data` y valida que el archivo sea un CSV válido con el formato requerido.

#### **Archivos de Datos Disponibles**
- **`grafos_light.csv`**: Archivo principal con 11 conexiones (carga inicial)
- **`grafos_full.csv`**: Archivo de test con **10,000+ conexiones** y **200+ nodos únicos**

## 🔗 Endpoints de la API

### 1. Estado del Grafo
```http
GET /grafo/estado
```
**Descripción**: Obtiene información sobre el grafo cargado en memoria
**Respuesta**: Total de conexiones, ubicaciones únicas y lista de conexiones

### 2. Cálculo de Ruta
```http
GET /grafo/tiempo/{origen}/{destino}
```
**Descripción**: Calcula la ruta más rápida entre dos ubicaciones
**Parámetros**:
- `origen`: Ubicación de origen (ej: "CP1")
- `destino`: Ubicación de destino (ej: "R20")

**Respuesta**:
```json
{
  "origen": "CP1",
  "destino": "R20",
  "ruta": ["CP1", "CP2", "R20"],
  "tiempoTotal": 74
}
```

### 3. Carga de Archivo CSV
```http
POST /bulk-upload
Content-Type: multipart/form-data
```
**Descripción**: Carga un archivo CSV y reemplaza el grafo en memoria
**Parámetros**:
- `file`: Archivo CSV con formato `loc_start;loc_end;time`

## 🧮 Algoritmo de Dijkstra

### Características
- **Complejidad temporal**: O(V²) donde V es el número de vértices
- **Complejidad espacial**: O(V)
- **Optimalidad**: Garantiza la ruta más corta en grafos con pesos no negativos
- **Implementación**: Priority Queue para optimización

### Ventajas para este Proyecto
- ✅ **Eficiencia**: Rápido para grafos de tamaño moderado
- ✅ **Precisión**: Garantiza la ruta óptima
- ✅ **Simplicidad**: Fácil de implementar y mantener
- ✅ **Escalabilidad**: Funciona bien con miles de nodos

### Limitaciones
- ⚠️ **Pesos negativos**: No funciona con tiempos negativos
- ⚠️ **Grafos muy grandes**: Puede ser lento con millones de nodos

## 📈 Performance

### Métricas de Rendimiento
- **Tiempo de respuesta**: < 300ms para rutas típicas
- **Carga de archivos**: Soporte para archivos de hasta 10MB
- **Memoria**: Optimizado para grafos con miles de conexiones

### Tests de Performance
- ✅ **12 tests unitarios** pasando exitosamente
- ✅ **Tests de carga** con 10,000+ conexiones
- ✅ **Validación de tiempos** de respuesta

## 🔧 Configuración

### Archivo `application.properties`
```properties
# Configuración de OpenAPI/Swagger
springdoc.swagger-ui.path=/swagger-ui/index.html

# Configuración de logging
logging.level.cl.randall=INFO
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n

# Configuración del servidor
server.port=8080
```

## 🐳 Docker

### **Configuración de Contenedores**

#### **Dockerfile**
- **Build stage**: `gradle:8.5-jdk21` para compilación
- **Runtime stage**: `openjdk:21-jdk-slim-bullseye` para ejecución
- **Multi-stage build** para optimizar tamaño de imagen
- **Usuario no-root** para seguridad
- **Health check** integrado

#### **Docker Compose**
- **Servicio**: `randall-it`
- **Puerto**: 8080
- **Volúmenes**: Logs y datos CSV
- **Health check**: Verificación automática de estado
- **Restart policy**: `unless-stopped`

#### **Variables de Entorno**
```bash
JAVA_OPTS=-Xmx512m -Xms256m
SPRING_PROFILES_ACTIVE=docker
```

#### **Archivos de Configuración**
- **`Dockerfile`**: Configuración del contenedor
- **`docker-compose.yml`**: Orquestación de servicios
- **`.dockerignore`**: Archivos excluidos del build
- **`docker-build.sh`**: Script de automatización
- **`application-docker.properties`**: Configuración específica para Docker

## 🧪 Testing

Para información detallada sobre los tests, consulta la [Documentación de Tests](TEST_DOCUMENTATION.md).

### **Carga de Datos en Tests**
Los tests utilizan diferentes estrategias de carga de datos:
- **Tests unitarios**: Utilizan `grafos_full.csv` con **200+ nodos** y **10,000+ conexiones** para validar performance
- **Tests de controladores**: Utilizan mocks para aislar la lógica de testing
- **Validación de performance**: Garantizan tiempos de respuesta < 300ms

### Ejecutar Tests
```bash
# Ejecutar todos los tests
gradle test

# Ejecutar tests con logs detallados
gradle test --info

# Ejecutar tests específicos
gradle test --tests GrafoServiceSimpleTest
```

## 📚 Documentación Adicional

- **[Documentación de Tests](TEST_DOCUMENTATION.md)**: Detalles completos sobre testing
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 🤝 Contribución

### Estándares de Código
- **Java 21**: Usar características modernas del lenguaje
- **Spring Boot**: Seguir convenciones del framework
- **Clean Code**: Código limpio y mantenible
- **Tests**: Cobertura completa de funcionalidades

### Proceso de Desarrollo
1. Crear feature branch
2. Implementar funcionalidad
3. Agregar tests
4. Ejecutar `gradle test`
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo [LICENSE](LICENSE) para más detalles.

## 👥 Equipo

- **Randall IT Team**
- **Email**: info@randall-it.cl

## 🆘 Soporte

Para reportar bugs o solicitar nuevas funcionalidades:
1. Crear un Issue en el repositorio
2. Incluir información detallada del problema
3. Adjuntar logs si es necesario

---

**Randall IT** - Optimizando rutas con algoritmos eficientes 🚀
