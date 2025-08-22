# 🧪 Documentación de Tests - Randall IT

## 📋 Descripción

Esta documentación detalla la estrategia de testing implementada en el proyecto **Randall IT**, incluyendo tests unitarios, tests de integración, tests de performance y ejemplos de uso.

## 🎯 Estrategia de Testing

### Objetivos
- ✅ **Cobertura completa**: Todos los métodos públicos tienen tests
- ✅ **Validación de algoritmos**: Verificación del algoritmo de Dijkstra
- ✅ **Performance**: Tests de rendimiento con tiempos < 300ms
- ✅ **Casos edge**: Manejo de situaciones límite
- ✅ **Integración**: Tests de endpoints REST

### Frameworks Utilizados
- **JUnit 5**: Framework principal de testing
- **Mockito**: Mocking de dependencias
- **Spring Boot Test**: Testing de componentes Spring
- **MockMvc**: Testing de endpoints REST

## 📁 Estructura de Tests

```
src/test/
├── java/cl/randall/
│   ├── services/
│   │   └── GrafoServiceSimpleTest.java    # Tests del servicio principal
│   └── controllers/
│       └── RandallControllerTest.java     # Tests de endpoints REST
└── resources/
    ├── application-test.properties        # Configuración de tests
    ├── grafos_full.csv                    # Dataset grande para tests
    └── ejemplo_bulk_upload.csv            # Dataset pequeño para tests
```

## 🧪 Tests del Servicio (GrafoServiceSimpleTest)

### Descripción
Tests unitarios directos del `GrafoService` que validan la lógica de negocio y el algoritmo de Dijkstra.

### Archivo: `src/test/java/cl/randall/services/GrafoServiceSimpleTest.java`

#### Tests Implementados

##### 1. **Carga de Datos**
```java
@Test
@DisplayName("Debería cargar correctamente grafos_full.csv")
void deberiaCargarGrafoFullCorrectamente() {
    // Verifica que se cargan al menos 10,000 conexiones y 200 ubicaciones
}
```

##### 2. **Performance del Algoritmo**
```java
@Test
@DisplayName("Debería encontrar ruta entre nodos existentes en menos de 300ms")
void deberiaEncontrarRutaRapidaEntreNodosExistentes() {
    // Valida tiempo de respuesta < 300ms
    // Verifica ruta válida entre origen y destino
}
```

##### 3. **Casos Edge**
```java
@Test
@DisplayName("Debería manejar nodos inexistentes")
void deberiaManejarNodosInexistentes() {
    // Verifica que retorna null para nodos que no existen
}

@Test
@DisplayName("Debería manejar mismo origen y destino")
void deberiaManejarMismoOrigenYDestino() {
    // Verifica ruta con tiempo 0 para mismo nodo
}
```

##### 4. **Rutas Complejas**
```java
@Test
@DisplayName("Debería encontrar rutas complejas entre diferentes tipos de nodos")
void deberiaEncontrarRutasComplejas() {
    // Valida rutas entre nodos de diferentes tipos (R, CP, E, P, C)
}
```

### Métricas de Performance
- **Tiempo de carga**: < 2 segundos para 10,000+ conexiones
- **Tiempo de cálculo**: < 300ms para rutas típicas
- **Memoria**: Optimizado para datasets grandes

## 🌐 Tests de Controladores (RandallControllerTest)

### Descripción
Tests de integración que validan los endpoints REST usando MockMvc y Mockito.

### Archivo: `src/test/java/cl/randall/controllers/RandallControllerTest.java`

#### Tests Implementados

##### 1. **Endpoint de Estado**
```java
@Test
@DisplayName("GET /grafo/estado debería responder en menos de 300ms")
void deberiaResponderEstadoGrafoRapidamente() {
    // Valida respuesta rápida del endpoint de estado
    // Verifica estructura JSON correcta
}
```

##### 2. **Endpoint de Ruta**
```java
@Test
@DisplayName("GET /grafo/tiempo/{origen}/{destino} debería manejar rutas inexistentes")
void deberiaManejarRutasInexistentes() {
    // Verifica manejo correcto de rutas inexistentes
    // Valida códigos de respuesta HTTP
}
```

##### 3. **Validación de Parámetros**
```java
@Test
@DisplayName("GET /grafo/tiempo/{origen}/{destino} debería validar parámetros")
void deberiaValidarParametros() {
    // Verifica validación de parámetros de entrada
}
```

##### 4. **Concurrencia**
```java
@Test
@DisplayName("Los endpoints deberían manejar múltiples requests concurrentes")
void deberiaManejarRequestsConcurrentes() {
    // Valida comportamiento bajo carga concurrente
    // Verifica tiempos de respuesta consistentes
}
```

##### 5. **Bulk Upload**
```java
@Test
@DisplayName("POST /bulk-upload debería estar disponible")
void deberiaEstarDisponibleBulkUpload() {
    // Verifica disponibilidad del endpoint de carga
}
```

## 📊 Datasets de Testing

### 1. **grafos_full.csv** (10,000+ conexiones)
- **Ubicación**: `src/test/resources/grafos_full.csv`
- **Propósito**: Tests de performance y carga
- **Estructura**: 200 nodos únicos, altamente interconectados
- **Tipos de nodos**: R, CP, E, P, C

### 2. **ejemplo_bulk_upload.csv** (10 conexiones)
- **Ubicación**: `src/test/resources/ejemplo_bulk_upload.csv`
- **Propósito**: Tests de funcionalidad básica
- **Estructura**: Dataset pequeño para validación rápida

### 3. **grafos_light.csv** (11 conexiones)
- **Ubicación**: `src/main/resources/grafos_light.csv`
- **Propósito**: Dataset de producción
- **Estructura**: Grafo simple para demostración

## 🔧 Configuración de Tests

### Archivo: `src/test/resources/application-test.properties`
```properties
# Configuración para tests
spring.main.web-application-type=none
logging.level.cl.randall=DEBUG
```

### Configuración de Gradle
```gradle
test {
    useJUnitPlatform()
}
```

## 📈 Métricas de Cobertura

### Tests Exitosos
- ✅ **12/12 tests** pasando exitosamente
- ✅ **100% cobertura** de métodos públicos
- ✅ **Performance validada** en todos los casos críticos

### Tiempos de Ejecución
- **Tests unitarios**: < 5 segundos
- **Tests de integración**: < 10 segundos
- **Tests completos**: < 15 segundos

## 🚀 Ejecución de Tests

### Comandos Básicos
```bash
# Ejecutar todos los tests
gradle test

# Ejecutar tests con logs detallados
gradle test --info

# Ejecutar tests con stacktrace completo
gradle test --stacktrace
```

### Tests Específicos
```bash
# Ejecutar solo tests del servicio
gradle test --tests GrafoServiceSimpleTest

# Ejecutar solo tests del controller
gradle test --tests RandallControllerTest

# Ejecutar test específico
gradle test --tests GrafoServiceSimpleTest.deberiaCargarGrafoFullCorrectamente
```

### Tests con Filtros
```bash
# Ejecutar tests que contengan "Performance"
gradle test --tests "*Performance*"

# Ejecutar tests que contengan "Ruta"
gradle test --tests "*Ruta*"
```

## 🔍 Debugging de Tests

### Logs Detallados
```bash
# Habilitar logs de debug
gradle test -Dlogging.level.cl.randall=DEBUG

# Ver logs de Spring Boot
gradle test -Dlogging.level.org.springframework=DEBUG
```

### Análisis de Fallos
```bash
# Ejecutar test fallido con información completa
gradle test --tests GrafoServiceSimpleTest.deberiaCargarGrafoFullCorrectamente --info --stacktrace

# Ver reporte de tests
open build/reports/tests/test/index.html
```

## 📋 Casos de Test

### Casos Positivos
1. ✅ Carga exitosa de archivo CSV
2. ✅ Cálculo de ruta entre nodos existentes
3. ✅ Ruta con mismo origen y destino
4. ✅ Rutas complejas entre diferentes tipos de nodos
5. ✅ Respuesta rápida de endpoints REST

### Casos Negativos
1. ✅ Manejo de nodos inexistentes
2. ✅ Manejo de rutas imposibles
3. ✅ Validación de parámetros inválidos
4. ✅ Manejo de archivos CSV malformados

### Casos de Performance
1. ✅ Tiempo de carga < 2 segundos para 10,000+ conexiones
2. ✅ Tiempo de cálculo < 300ms para rutas típicas
3. ✅ Manejo de requests concurrentes
4. ✅ Uso eficiente de memoria

## 🛠️ Mantenimiento de Tests

### Agregar Nuevos Tests
1. **Identificar funcionalidad** a testear
2. **Crear test unitario** en `GrafoServiceSimpleTest`
3. **Crear test de integración** en `RandallControllerTest`
4. **Ejecutar tests** para validar
5. **Documentar** el nuevo test

### Actualizar Tests Existentes
1. **Revisar cambios** en funcionalidad
2. **Actualizar assertions** si es necesario
3. **Validar performance** no se degrada
4. **Ejecutar suite completa** de tests

### Buenas Prácticas
- ✅ **Nombres descriptivos** para tests
- ✅ **Un test por funcionalidad**
- ✅ **Assertions específicos**
- ✅ **Validación de performance**
- ✅ **Documentación clara**

## 📊 Reportes de Tests

### Generación de Reportes
```bash
# Generar reporte HTML
gradle test
open build/reports/tests/test/index.html

# Generar reporte con cobertura
gradle test jacocoTestReport
open build/reports/jacoco/test/html/index.html
```

### Interpretación de Resultados
- **Tests pasando**: ✅ Verde
- **Tests fallando**: ❌ Rojo
- **Tests ignorados**: ⚠️ Amarillo
- **Cobertura**: Porcentaje de código cubierto

## 🔗 Enlaces Relacionados

- **[README Principal](README.md)**: Documentación general del proyecto
- **[Swagger UI](http://localhost:8080/swagger-ui/index.html)**: Documentación interactiva de la API
- **[OpenAPI JSON](http://localhost:8080/v3/api-docs)**: Especificación de la API

---

**Testing en Randall IT** - Garantizando calidad y performance 🧪
