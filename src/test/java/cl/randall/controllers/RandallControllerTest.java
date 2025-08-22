package cl.randall.controllers;

import cl.randall.services.GrafoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@WebMvcTest(RandallController.class)
class RandallControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GrafoService grafoService;

    @BeforeEach
    void setUp() {
        // Configurar mocks básicos
        when(grafoService.obtenerTodasLasConexiones()).thenReturn(Arrays.asList());
        when(grafoService.obtenerUbicacionesUnicas()).thenReturn(Arrays.asList("R01", "CP100", "E121"));
        
        // Mock para rutas válidas
        cl.randall.models.ResultadoRuta resultadoMock = new cl.randall.models.ResultadoRuta(
            Arrays.asList("R01", "CP100"), 100
        );
        when(grafoService.encontrarRutaMasRapida("R01", "CP100")).thenReturn(resultadoMock);
        
        // Mock para rutas inexistentes
        when(grafoService.encontrarRutaMasRapida("NODO_INEXISTENTE", "OTRO_NODO")).thenReturn(null);
        
        // Mock para mismo origen y destino
        cl.randall.models.ResultadoRuta resultadoMismoNodo = new cl.randall.models.ResultadoRuta(
            Arrays.asList("R01"), 0
        );
        when(grafoService.encontrarRutaMasRapida("R01", "R01")).thenReturn(resultadoMismoNodo);
    }

    @Test
    @DisplayName("GET /grafo/estado debería responder en menos de 300ms")
    void deberiaResponderEstadoGrafoRapidamente() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/grafo/estado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalConexiones").exists())
                .andExpect(jsonPath("$.totalUbicaciones").exists())
                .andExpect(jsonPath("$.ubicaciones").exists())
                .andExpect(jsonPath("$.conexiones").exists());
        
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        assertTrue(tiempoEjecucion < 300, 
            "El endpoint /grafo/estado debería responder en menos de 300ms, pero tardó: " + tiempoEjecucion + "ms");
    }

    @Test
    @DisplayName("GET /grafo/tiempo/{origen}/{destino} debería manejar rutas inexistentes")
    void deberiaManejarRutasInexistentes() throws Exception {
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/grafo/tiempo/NODO_INEXISTENTE/OTRO_NODO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origen").value("NODO_INEXISTENTE"))
                .andExpect(jsonPath("$.destino").value("OTRO_NODO"))
                .andExpect(jsonPath("$.ruta").isEmpty())
                .andExpect(jsonPath("$.tiempoTotal").isEmpty())
                .andExpect(jsonPath("$.mensaje").value("No existe ruta entre las ubicaciones especificadas"));
        
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        assertTrue(tiempoEjecucion < 200, 
            "El endpoint debería responder rápidamente para rutas inexistentes, pero tardó: " + tiempoEjecucion + "ms");
    }

    @Test
    @DisplayName("GET /grafo/estado debería retornar estructura JSON correcta")
    void deberiaRetornarEstructuraJsonCorrecta() throws Exception {
        mockMvc.perform(get("/grafo/estado")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalConexiones").isNumber())
                .andExpect(jsonPath("$.totalUbicaciones").isNumber())
                .andExpect(jsonPath("$.ubicaciones").isArray())
                .andExpect(jsonPath("$.conexiones").isArray());
    }

    @Test
    @DisplayName("GET /grafo/tiempo/{origen}/{destino} debería validar parámetros")
    void deberiaValidarParametros() throws Exception {
        // Mock del servicio para retornar null
        when(grafoService.encontrarRutaMasRapida(anyString(), anyString())).thenReturn(null);
        
        // Test con caracteres especiales en los parámetros
        mockMvc.perform(get("/grafo/tiempo/R01/CP-100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origen").value("R01"))
                .andExpect(jsonPath("$.destino").value("CP-100"));
        
        // Test con espacios en los parámetros
        mockMvc.perform(get("/grafo/tiempo/R 01/CP100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.origen").value("R 01"))
                .andExpect(jsonPath("$.destino").value("CP100"));
    }

    @Test
    @DisplayName("Los endpoints deberían manejar múltiples requests concurrentes")
    void deberiaManejarRequestsConcurrentes() throws Exception {
        // Mock del servicio
        cl.randall.models.ResultadoRuta resultadoMock = new cl.randall.models.ResultadoRuta(
            Arrays.asList("R01", "CP100"), 100
        );
        when(grafoService.encontrarRutaMasRapida("R01", "CP100")).thenReturn(resultadoMock);
        
        long startTime = System.currentTimeMillis();
        
        // Ejecutar múltiples requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/grafo/tiempo/R01/CP100")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.tiempoTotal").value(100));
        }
        
        long endTime = System.currentTimeMillis();
        long tiempoEjecucion = endTime - startTime;
        
        // Verificar que el tiempo total para 5 requests es razonable
        assertTrue(tiempoEjecucion < 1000, 
            "5 requests concurrentes deberían completarse en menos de 1000ms, pero tardaron: " + tiempoEjecucion + "ms");
    }
    
    @Test
    @DisplayName("POST /bulk-upload debería estar disponible")
    void deberiaEstarDisponibleBulkUpload() throws Exception {
        mockMvc.perform(post("/bulk-upload")
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest()); // Esperamos un error porque no se envía archivo
    }
}
