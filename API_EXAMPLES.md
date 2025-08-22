# 🔗 Ejemplos de Uso - API Randall IT

## 📋 Descripción

Esta documentación proporciona ejemplos prácticos de cómo usar la API Randall IT para diferentes casos de uso, incluyendo comandos cURL, respuestas esperadas y escenarios comunes.

## 🚀 Configuración Inicial

### Iniciar la Aplicación
```bash
# Compilar y ejecutar
gradle bootRun

# Verificar que está funcionando
curl http://localhost:8080/grafo/estado
```

### **Carga Inicial de Datos**
Al arrancar la aplicación, se precargan automáticamente los datos del archivo `grafos_light.csv` que contiene **11 conexiones** entre **6 ubicaciones únicas**. Estos datos están disponibles inmediatamente para consultas.

### **Reemplazo de Datos**
Para cargar una nueva estructura de grafos, utiliza el endpoint **`POST /bulk-upload`** que reemplaza completamente los datos precargados con la nueva información del archivo CSV subido.

### URLs de Acceso
- **API Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## 📊 Endpoint: Estado del Grafo

### GET /grafo/estado

#### Ejemplo de Uso
```bash
curl -X GET http://localhost:8080/grafo/estado \
  -H "Content-Type: application/json"
```

#### Respuesta Esperada
```json
{
  "totalConexiones": 11,
  "totalUbicaciones": 6,
  "ubicaciones": ["R20", "R12", "R11", "CP2", "CP1", "R13"],
  "conexiones": [
    {
      "locStart": "R20",
      "locEnd": "R13",
      "time": 11
    },
    {
      "locStart": "R20",
      "locEnd": "CP2",
      "time": 60
    }
    // ... más conexiones
  ]
}
```

#### Casos de Uso
- ✅ **Verificar carga**: Confirmar que el grafo se cargó correctamente
- ✅ **Estadísticas**: Obtener información sobre el dataset
- ✅ **Debugging**: Verificar el estado de la aplicación

## 🛣️ Endpoint: Cálculo de Ruta

### GET /grafo/tiempo/{origen}/{destino}

#### Ejemplo 1: Ruta Simple
```bash
curl -X GET "http://localhost:8080/grafo/tiempo/CP1/R20" \
  -H "Content-Type: application/json"
```

**Respuesta**:
```json
{
  "origen": "CP1",
  "destino": "R20",
  "ruta": ["CP1", "CP2", "R20"],
  "tiempoTotal": 67
}
```

#### Ejemplo 2: Ruta Compleja
```bash
curl -X GET "http://localhost:8080/grafo/tiempo/R11/R13" \
  -H "Content-Type: application/json"
```

**Respuesta**:
```json
{
  "origen": "R11",
  "destino": "R13",
  "ruta": ["R11", "R12", "R13"],
  "tiempoTotal": 29
}
```

#### Ejemplo 3: Mismo Origen y Destino
```bash
curl -X GET "http://localhost:8080/grafo/tiempo/CP1/CP1" \
  -H "Content-Type: application/json"
```

**Respuesta**:
```json
{
  "origen": "CP1",
  "destino": "CP1",
  "ruta": ["CP1"],
  "tiempoTotal": 0
}
```

#### Ejemplo 4: Ruta Inexistente
```bash
curl -X GET "http://localhost:8080/grafo/tiempo/NODO_INEXISTENTE/OTRO_NODO" \
  -H "Content-Type: application/json"
```

**Respuesta**:
```json
{
  "origen": "NODO_INEXISTENTE",
  "destino": "OTRO_NODO",
  "ruta": null,
  "tiempoTotal": null,
  "mensaje": "No existe ruta entre las ubicaciones especificadas"
}
```

## 📤 Endpoint: Carga de Archivo CSV

### POST /bulk-upload

#### Ejemplo 1: Carga Exitosa
```bash
curl -X POST http://localhost:8080/bulk-upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@src/test/resources/ejemplo_bulk_upload.csv"
```

**Respuesta**:
```json
{
  "success": true,
  "mensaje": "Archivo procesado exitosamente",
  "nombreArchivo": "ejemplo_bulk_upload.csv",
  "tamañoArchivo": 150,
  "tiempoProcesamiento": "45ms",
  "totalConexiones": 10,
  "totalUbicaciones": 8
}
```

#### Ejemplo 2: Archivo Vacío
```bash
curl -X POST http://localhost:8080/bulk-upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@archivo_vacio.csv"
```

**Respuesta**:
```json
{
  "success": false,
  "mensaje": "El archivo está vacío"
}
```

#### Ejemplo 3: Archivo No CSV
```bash
curl -X POST http://localhost:8080/bulk-upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@archivo.txt"
```

**Respuesta**:
```json
{
  "success": false,
  "mensaje": "El archivo debe ser un CSV"
}
```

## 📁 Ejemplos de Archivos CSV

### Archivo de Ejemplo Básico
```csv
loc_start;loc_end;time
R01;R02;15
R02;R03;20
R03;CP100;25
CP100;E121;30
E121;P151;10
P151;C176;18
C176;R80;22
R80;CP140;28
CP140;E150;35
E150;P200;12
```

### Archivo con Errores (para testing)
```csv
loc_start;loc_end;time
R01;R02;15
R02;R03;abc
R03;CP100;25
CP100;E121
E121;P151;10
```

### **Datasets Disponibles**

#### **grafos_light.csv** (Carga Inicial)
- **Ubicación**: `src/main/resources/grafos_light.csv`
- **Conexiones**: 11
- **Nodos únicos**: 6
- **Propósito**: Datos mínimos para demostración y desarrollo

#### **grafos_full.csv** (Tests de Performance)
- **Ubicación**: `src/test/resources/grafos_full.csv`
- **Conexiones**: 10,000+
- **Nodos únicos**: 200+
- **Propósito**: Validación de performance con datasets grandes

## 🔧 Scripts de Automatización

### Script de Prueba Completa
```bash
#!/bin/bash
# test_api.sh

echo "🧪 Probando API Randall IT..."

# 1. Verificar estado
echo "📊 Verificando estado del grafo..."
curl -s http://localhost:8080/grafo/estado | jq '.'

# 2. Probar rutas
echo "🛣️ Probando cálculo de rutas..."
curl -s "http://localhost:8080/grafo/tiempo/CP1/R20" | jq '.'
curl -s "http://localhost:8080/grafo/tiempo/R11/R13" | jq '.'

# 3. Probar bulk upload
echo "📤 Probando carga de archivo..."
curl -s -X POST http://localhost:8080/bulk-upload \
  -H "Content-Type: multipart/form-data" \
  -F "file=@src/test/resources/ejemplo_bulk_upload.csv" | jq '.'

echo "✅ Pruebas completadas!"
```

### Script de Performance
```bash
#!/bin/bash
# performance_test.sh

echo "⚡ Test de Performance..."

# Test de tiempo de respuesta
start_time=$(date +%s%N)
curl -s "http://localhost:8080/grafo/tiempo/CP1/R20" > /dev/null
end_time=$(date +%s%N)

duration=$((end_time - start_time))
duration_ms=$((duration / 1000000))

echo "Tiempo de respuesta: ${duration_ms}ms"

if [ $duration_ms -lt 300 ]; then
    echo "✅ Performance OK (< 300ms)"
else
    echo "⚠️ Performance lenta (> 300ms)"
fi
```

## 🌐 Ejemplos con JavaScript

### Usando Fetch API
```javascript
// Obtener estado del grafo
async function getGrafoEstado() {
    const response = await fetch('http://localhost:8080/grafo/estado');
    const data = await response.json();
    console.log('Estado del grafo:', data);
}

// Calcular ruta
async function calcularRuta(origen, destino) {
    const response = await fetch(`http://localhost:8080/grafo/tiempo/${origen}/${destino}`);
    const data = await response.json();
    console.log(`Ruta de ${origen} a ${destino}:`, data);
}

// Cargar archivo CSV
async function cargarCSV(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    const response = await fetch('http://localhost:8080/bulk-upload', {
        method: 'POST',
        body: formData
    });
    
    const data = await response.json();
    console.log('Resultado de carga:', data);
}
```

### Usando Axios
```javascript
import axios from 'axios';

const API_BASE = 'http://localhost:8080';

// Cliente API
const apiClient = axios.create({
    baseURL: API_BASE,
    timeout: 5000
});

// Funciones de la API
export const grafoAPI = {
    // Obtener estado
    getEstado: () => apiClient.get('/grafo/estado'),
    
    // Calcular ruta
    calcularRuta: (origen, destino) => 
        apiClient.get(`/grafo/tiempo/${origen}/${destino}`),
    
    // Cargar CSV
    cargarCSV: (file) => {
        const formData = new FormData();
        formData.append('file', file);
        return apiClient.post('/bulk-upload', formData, {
            headers: { 'Content-Type': 'multipart/form-data' }
        });
    }
};
```

## 🐍 Ejemplos con Python

### Usando Requests
```python
import requests
import json

class RandallITClient:
    def __init__(self, base_url="http://localhost:8080"):
        self.base_url = base_url
    
    def get_estado(self):
        """Obtener estado del grafo"""
        response = requests.get(f"{self.base_url}/grafo/estado")
        return response.json()
    
    def calcular_ruta(self, origen, destino):
        """Calcular ruta más rápida"""
        response = requests.get(f"{self.base_url}/grafo/tiempo/{origen}/{destino}")
        return response.json()
    
    def cargar_csv(self, file_path):
        """Cargar archivo CSV"""
        with open(file_path, 'rb') as file:
            files = {'file': file}
            response = requests.post(f"{self.base_url}/bulk-upload", files=files)
        return response.json()

# Uso del cliente
client = RandallITClient()

# Ejemplos de uso
print("Estado del grafo:", client.get_estado())
print("Ruta CP1 -> R20:", client.calcular_ruta("CP1", "R20"))
print("Cargar CSV:", client.cargar_csv("ejemplo.csv"))
```

## 📊 Casos de Uso Reales

### 1. Sistema de Logística
```bash
# Calcular ruta de entrega
curl -X GET "http://localhost:8080/grafo/tiempo/ALMACEN/CENTRO_DISTRIBUCION"

# Respuesta esperada
{
  "origen": "ALMACEN",
  "destino": "CENTRO_DISTRIBUCION",
  "ruta": ["ALMACEN", "PUNTO_A", "PUNTO_B", "CENTRO_DISTRIBUCION"],
  "tiempoTotal": 120
}
```

### 2. Sistema de Transporte Público
```bash
# Calcular ruta de transporte
curl -X GET "http://localhost:8080/grafo/tiempo/ESTACION_A/ESTACION_Z"

# Respuesta esperada
{
  "origen": "ESTACION_A",
  "destino": "ESTACION_Z",
  "ruta": ["ESTACION_A", "ESTACION_B", "ESTACION_C", "ESTACION_Z"],
  "tiempoTotal": 45
}
```

### 3. Sistema de Redes
```bash
# Calcular ruta de red
curl -X GET "http://localhost:8080/grafo/tiempo/NODO_ORIGEN/NODO_DESTINO"

# Respuesta esperada
{
  "origen": "NODO_ORIGEN",
  "destino": "NODO_DESTINO",
  "ruta": ["NODO_ORIGEN", "ROUTER_1", "ROUTER_2", "NODO_DESTINO"],
  "tiempoTotal": 8
}
```

## 🔍 Debugging y Troubleshooting

### Verificar Logs
```bash
# Ver logs de la aplicación
tail -f logs/application.log

# Ver logs específicos
grep "ERROR" logs/application.log
grep "WARN" logs/application.log
```

### Verificar Performance
```bash
# Test de latencia
time curl -s http://localhost:8080/grafo/estado > /dev/null

# Test de concurrencia
for i in {1..10}; do
    curl -s "http://localhost:8080/grafo/tiempo/CP1/R20" &
done
wait
```

### Verificar Datos
```bash
# Verificar archivo CSV
head -5 src/main/resources/grafos_light.csv

# Verificar estructura JSON
curl -s http://localhost:8080/grafo/estado | jq '.conexiones | length'
```

## 📚 Recursos Adicionales

- **[README Principal](README.md)**: Documentación general
- **[Documentación de Tests](TEST_DOCUMENTATION.md)**: Guía de testing
- **[Swagger UI](http://localhost:8080/swagger-ui/index.html)**: Documentación interactiva

---

**Ejemplos de Uso Randall IT** - Guía práctica para desarrolladores 🔗
