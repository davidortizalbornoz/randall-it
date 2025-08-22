package cl.randall.services;

import cl.randall.models.ConexionGrafo;
import cl.randall.models.ResultadoRuta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import org.springframework.web.multipart.MultipartFile;

// Servicio para gestionar los datos del grafo cargados desde el archivo CSV
@Service
public class GrafoService {
    
    private static final Logger logger = LoggerFactory.getLogger(GrafoService.class);
    
    private final Map<String, List<ConexionGrafo>> grafoPorOrigen;
    
    public GrafoService() {
        this("grafos_light.csv");
    }
    
    public GrafoService(String nombreArchivo) {
        this.grafoPorOrigen = new HashMap<>();
        cargarGrafoDesdeCSV(nombreArchivo);
    }
    
    // Carga los datos del grafo desde el archivo CSV especificado
    private void cargarGrafoDesdeCSV(String nombreArchivo) {
        try {
            ClassPathResource resource = new ClassPathResource(nombreArchivo);
            procesarCSV(resource.getInputStream(), nombreArchivo);
            logger.info("Grafo cargado exitosamente desde {}. Total de conexiones: {}", 
                nombreArchivo, obtenerTodasLasConexiones().size());
        } catch (IOException e) {
            logger.error("Error al cargar el archivo {}", nombreArchivo, e);
            throw new RuntimeException("No se pudo cargar el archivo de grafos: " + nombreArchivo, e);
        }
    }
    
    // Procesa un archivo CSV desde un InputStream
    private void procesarCSV(java.io.InputStream inputStream, String nombreArchivo) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            String linea;
            boolean primeraLinea = true;
            int lineasProcesadas = 0;
            int errores = 0;
            
            while ((linea = reader.readLine()) != null) {
                // Saltar la línea de encabezados
                if (primeraLinea) {
                    primeraLinea = false;
                    continue;
                }
                
                String[] campos = linea.split(";");
                if (campos.length == 3) {
                    try {
                        ConexionGrafo conexion = new ConexionGrafo(
                            campos[0].trim(),
                            campos[1].trim(),
                            Integer.parseInt(campos[2].trim())
                        );
                        
                        // Agrupar por origen para facilitar consultas
                        grafoPorOrigen.computeIfAbsent(conexion.getLocStart(), k -> new ArrayList<>())
                                     .add(conexion);
                        
                        lineasProcesadas++;
                        
                    } catch (NumberFormatException e) {
                        logger.warn("Error al parsear el tiempo en la línea: {}", linea, e);
                        errores++;
                    }
                } else {
                    logger.warn("Línea con formato incorrecto: {}", linea);
                    errores++;
                }
            }
            
            if (nombreArchivo != null) {
                logger.info("Archivo {} procesado. Líneas procesadas: {}, Errores: {}", 
                    nombreArchivo, lineasProcesadas, errores);
            }
        }
    }
    
    // Obtiene todas las conexiones del grafo
    public List<ConexionGrafo> obtenerTodasLasConexiones() {
        List<ConexionGrafo> todasLasConexiones = new ArrayList<>();
        for (List<ConexionGrafo> conexiones : grafoPorOrigen.values()) {
            todasLasConexiones.addAll(conexiones);
        }
        return todasLasConexiones;
    }
    
    // Obtiene todas las ubicaciones únicas del grafo
    public List<String> obtenerUbicacionesUnicas() {
        return new ArrayList<>(grafoPorOrigen.keySet());
    }
    
    // Encuentra la ruta más rápida entre dos ubicaciones usando el algoritmo de Dijkstra
    public ResultadoRuta encontrarRutaMasRapida(String origen, String destino) {
        // Verificar que ambos nodos existen en el grafo
        if (!grafoPorOrigen.containsKey(origen) || !existeNodo(destino)) {
            return null;
        }
        
        // Si es la misma ubicación
        if (origen.equals(destino)) {
            return new ResultadoRuta(Arrays.asList(origen), 0);
        }
        
        // Estructuras para el algoritmo de Dijkstra
        Map<String, Integer> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        PriorityQueue<Map.Entry<String, Integer>> cola = new PriorityQueue<>(
            (a, b) -> Integer.compare(a.getValue(), b.getValue())
        );
        
        // Inicializar distancias
        for (String nodo : obtenerTodosLosNodos()) {
            distancias.put(nodo, Integer.MAX_VALUE);
        }
        distancias.put(origen, 0);
        
        // Agregar origen a la cola
        cola.offer(new AbstractMap.SimpleEntry<>(origen, 0));
        
        while (!cola.isEmpty()) {
            Map.Entry<String, Integer> actual = cola.poll();
            String nodoActual = actual.getKey();
            int distanciaActual = actual.getValue();
            
            // Si ya procesamos este nodo con una distancia menor, continuar
            if (distanciaActual > distancias.get(nodoActual)) {
                continue;
            }
            
            // Si llegamos al destino, hemos encontrado la ruta más corta
            if (nodoActual.equals(destino)) {
                break;
            }
            
            // Explorar vecinos
            List<ConexionGrafo> conexiones = grafoPorOrigen.get(nodoActual);
            if (conexiones != null) {
                for (ConexionGrafo conexion : conexiones) {
                    String vecino = conexion.getLocEnd();
                    int nuevaDistancia = distanciaActual + conexion.getTime();
                    
                    if (nuevaDistancia < distancias.get(vecino)) {
                        distancias.put(vecino, nuevaDistancia);
                        predecesores.put(vecino, nodoActual);
                        cola.offer(new AbstractMap.SimpleEntry<>(vecino, nuevaDistancia));
                    }
                }
            }
        }
        
        // Si no se encontró ruta al destino
        if (distancias.get(destino) == Integer.MAX_VALUE) {
            return null;
        }
        
        // Reconstruir la ruta
        List<String> ruta = new ArrayList<>();
        String nodoActual = destino;
        while (nodoActual != null) {
            ruta.add(0, nodoActual);
            nodoActual = predecesores.get(nodoActual);
        }
        
        return new ResultadoRuta(ruta, distancias.get(destino));
    }
    
    // Verifica si un nodo existe en el grafo (como origen o destino)
    private boolean existeNodo(String nodo) {
        // Verificar si es un nodo origen
        if (grafoPorOrigen.containsKey(nodo)) {
            return true;
        }
        
        // Verificar si es un nodo destino
        for (List<ConexionGrafo> conexiones : grafoPorOrigen.values()) {
            for (ConexionGrafo conexion : conexiones) {
                if (conexion.getLocEnd().equals(nodo)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // Obtiene todos los nodos únicos del grafo (origenes y destinos)
    private Set<String> obtenerTodosLosNodos() {
        Set<String> nodos = new HashSet<>();
        
        // Agregar todos los nodos origen
        nodos.addAll(grafoPorOrigen.keySet());
        
        // Agregar todos los nodos destino
        for (List<ConexionGrafo> conexiones : grafoPorOrigen.values()) {
            for (ConexionGrafo conexion : conexiones) {
                nodos.add(conexion.getLocEnd());
            }
        }
        
        return nodos;
    }
    
    // Carga un grafo desde un archivo CSV subido y reemplaza el grafo actual
    public boolean cargarGrafoDesdeArchivo(MultipartFile file) throws IOException {
        try {
            // Limpiar el grafo actual
            grafoPorOrigen.clear();
            
            // Procesar el nuevo archivo usando el método común
            procesarCSV(file.getInputStream(), file.getOriginalFilename());
            
            int totalConexiones = obtenerTodasLasConexiones().size();
            logger.info("Archivo {} procesado exitosamente. Total conexiones: {}", 
                file.getOriginalFilename(), totalConexiones);
            
            return totalConexiones > 0; // Retorna true si se procesó al menos una línea válida
            
        } catch (Exception e) {
            logger.error("Error al procesar el archivo CSV", e);
            return false;
        }
    }
}
