package cl.randall.services;

import cl.randall.models.ConexionGrafo;
import cl.randall.models.ResultadoRuta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GrafoServiceSimpleTest {

    private GrafoService grafoService;

    @BeforeEach
    void setUp() {
        grafoService = new GrafoService("grafos_full.csv");
    }

    @Test
    @DisplayName("Debería cargar correctamente grafos_full.csv")
    void deberiaCargarGrafoFullCorrectamente() {
        // Verificar que se cargaron las conexiones
        List<ConexionGrafo> todasLasConexiones = grafoService.obtenerTodasLasConexiones();
        assertNotNull(todasLasConexiones);
        assertTrue(todasLasConexiones.size() >= 10000, 
            "Debería tener al menos 10,000 conexiones, pero tiene: " + todasLasConexiones.size());
        
        // Verificar que se cargaron las ubicaciones únicas
        List<String> ubicaciones = grafoService.obtenerUbicacionesUnicas();
        assertNotNull(ubicaciones);
        assertTrue(ubicaciones.size() >= 200, 
            "Debería tener al menos 200 ubicaciones únicas, pero tiene: " + ubicaciones.size());
    }

    @Test
    @DisplayName("Debería encontrar ruta entre nodos existentes en menos de 300ms")
    void deberiaEncontrarRutaRapidaEntreNodosExistentes() {
        String origen = "R01";
        String destino = "CP100";
        
        long startTime = System.currentTimeMillis();
        ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(origen, destino);
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        // Verificar tiempo de respuesta
        assertTrue(tiempoEjecucion < 300, 
            "El tiempo de ejecución debería ser menor a 300ms, pero fue: " + tiempoEjecucion + "ms");
        
        // Verificar que se encontró una ruta
        assertNotNull(resultado, "Debería encontrar una ruta entre " + origen + " y " + destino);
        assertNotNull(resultado.getRuta(), "La ruta no debería ser null");
        assertTrue(resultado.getRuta().size() >= 2, "La ruta debería tener al menos 2 nodos");
        assertTrue(resultado.getTiempoTotal() > 0, "El tiempo total debería ser mayor a 0");
        
        // Verificar que la ruta comienza y termina correctamente
        assertEquals(origen, resultado.getRuta().get(0), "La ruta debería comenzar en el origen");
        assertEquals(destino, resultado.getRuta().get(resultado.getRuta().size() - 1), 
            "La ruta debería terminar en el destino");
    }

    @Test
    @DisplayName("Debería encontrar ruta compleja entre nodos distantes en menos de 300ms")
    void deberiaEncontrarRutaComplejaEntreNodosDistantes() {
        String origen = "R01";
        String destino = "C198";
        
        long startTime = System.currentTimeMillis();
        ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(origen, destino);
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        // Verificar tiempo de respuesta
        assertTrue(tiempoEjecucion < 300, 
            "El tiempo de ejecución debería ser menor a 300ms, pero fue: " + tiempoEjecucion + "ms");
        
        // Verificar que se encontró una ruta
        assertNotNull(resultado, "Debería encontrar una ruta entre " + origen + " y " + destino);
        assertTrue(resultado.getRuta().size() >= 3, 
            "Una ruta compleja debería tener al menos 3 nodos, pero tiene: " + resultado.getRuta().size());
        assertTrue(resultado.getTiempoTotal() > 50, 
            "Una ruta compleja debería tener tiempo total mayor a 50 minutos");
    }

    @Test
    @DisplayName("Debería retornar null para nodos inexistentes")
    void deberiaRetornarNullParaNodosInexistentes() {
        String origen = "NODO_INEXISTENTE";
        String destino = "OTRO_NODO_INEXISTENTE";
        
        long startTime = System.currentTimeMillis();
        ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(origen, destino);
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        // Verificar tiempo de respuesta (debería ser muy rápido)
        assertTrue(tiempoEjecucion < 100, 
            "El tiempo de ejecución para nodos inexistentes debería ser menor a 100ms, pero fue: " + tiempoEjecucion + "ms");
        
        // Verificar que retorna null
        assertNull(resultado, "Debería retornar null para nodos inexistentes");
    }

    @Test
    @DisplayName("Debería manejar ruta desde y hacia el mismo nodo")
    void deberiaManejarRutaMismoNodo() {
        String nodo = "R01";
        
        long startTime = System.currentTimeMillis();
        ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(nodo, nodo);
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        // Verificar tiempo de respuesta
        assertTrue(tiempoEjecucion < 100, 
            "El tiempo de ejecución debería ser menor a 100ms, pero fue: " + tiempoEjecucion + "ms");
        
        // Verificar resultado
        assertNotNull(resultado, "Debería retornar un resultado válido");
        assertEquals(1, resultado.getRuta().size(), "La ruta debería tener exactamente 1 nodo");
        assertEquals(nodo, resultado.getRuta().get(0), "La ruta debería contener el nodo original");
        assertEquals(0, resultado.getTiempoTotal(), "El tiempo total debería ser 0");
    }

    @Test
    @DisplayName("Debería encontrar rutas entre diferentes tipos de nodos")
    void deberiaEncontrarRutasEntreDiferentesTiposNodos() {
        // Test múltiples combinaciones de tipos de nodos
        String[][] combinaciones = {
            {"R01", "CP105"},    // Ruta -> Centro de Población
            {"E125", "C176"},    // Estación -> Cruce
            {"CP105", "P151"},   // Centro de Población -> Paradero
            {"R23", "E150"},     // Ruta -> Estación
            {"C176", "CP96"}     // Cruce -> Centro de Población
        };
        
        for (String[] combinacion : combinaciones) {
            String origen = combinacion[0];
            String destino = combinacion[1];
            
            long startTime = System.currentTimeMillis();
            ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(origen, destino);
            long endTime = System.currentTimeMillis();
            long tiempoEjecucion = endTime - startTime;
            
            // Verificar tiempo de respuesta
            assertTrue(tiempoEjecucion < 300, 
                "Tiempo de ejecución para " + origen + " -> " + destino + " debería ser < 300ms, pero fue: " + tiempoEjecucion + "ms");
            
            // Verificar que se encontró una ruta
            assertNotNull(resultado, "Debería encontrar ruta entre " + origen + " y " + destino);
            assertTrue(resultado.getRuta().size() >= 2, 
                "La ruta entre " + origen + " y " + destino + " debería tener al menos 2 nodos");
            assertTrue(resultado.getTiempoTotal() > 0, 
                "El tiempo total entre " + origen + " y " + destino + " debería ser mayor a 0");
        }
    }
}
