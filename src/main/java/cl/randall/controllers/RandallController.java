package cl.randall.controllers;

import cl.randall.models.ConexionGrafo;
import cl.randall.models.ResultadoRuta;
import cl.randall.services.GrafoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Grafos", description = "API para gestión de grafos y cálculo de rutas")
public class RandallController {

    private static final Logger logger = LoggerFactory.getLogger(RandallController.class);
    
    private final GrafoService grafoService;
    
    @Autowired
    public RandallController(GrafoService grafoService) {
        this.grafoService = grafoService;
    }
    
    @Operation(summary = "Obtener estado del grafo", description = "Retorna información sobre el grafo cargado en memoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del grafo obtenido exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class)))
    })
    @RequestMapping(value = "/grafo/estado", method = RequestMethod.GET)
    public ResponseEntity<?> obtenerEstadoGrafo() {
        List<ConexionGrafo> todasLasConexiones = grafoService.obtenerTodasLasConexiones();
        List<String> ubicaciones = grafoService.obtenerUbicacionesUnicas();
        
        return ResponseEntity.ok(Map.of(
            "totalConexiones", todasLasConexiones.size(),
            "totalUbicaciones", ubicaciones.size(),
            "ubicaciones", ubicaciones,
            "conexiones", todasLasConexiones
        ));
    }
    
    
    @Operation(summary = "Calcular ruta más rápida", description = "Calcula la ruta más rápida entre dos ubicaciones usando el algoritmo de Dijkstra")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ruta calculada exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "404", description = "No se encontró ruta entre las ubicaciones")
    })
    @RequestMapping(value = "/grafo/tiempo/{origen}/{destino}", method = RequestMethod.GET)
    public ResponseEntity<?> obtenerTiempoDirecto(
            @Parameter(description = "Ubicación de origen", example = "CP1") @PathVariable String origen,
            @Parameter(description = "Ubicación de destino", example = "R20") @PathVariable String destino) {
        ResultadoRuta resultado = grafoService.encontrarRutaMasRapida(origen, destino);
        
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("origen", origen);
        respuesta.put("destino", destino);
        
        if (resultado != null) {
            respuesta.put("ruta", resultado.getRuta());
            respuesta.put("tiempoTotal", resultado.getTiempoTotal());
        } else {
            respuesta.put("ruta", null);
            respuesta.put("tiempoTotal", null);
            respuesta.put("mensaje", "No existe ruta entre las ubicaciones especificadas");
        }
        
        return ResponseEntity.ok(respuesta);
    }
    
    @Operation(summary = "Cargar archivo CSV", description = "Carga un archivo CSV y reemplaza el grafo en memoria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Archivo procesado exitosamente",
            content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "Archivo inválido o vacío"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @RequestMapping(value = "/bulk-upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUpload(
            @Parameter(description = "Archivo CSV con formato: loc_start;loc_end;time") 
            @RequestParam("file") MultipartFile file) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                respuesta.put("success", false);
                respuesta.put("mensaje", "El archivo está vacío");
                return ResponseEntity.badRequest().body(respuesta);
            }
            
            // Validar que sea un archivo CSV
            String nombreArchivo = file.getOriginalFilename();
            if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".csv")) {
                respuesta.put("success", false);
                respuesta.put("mensaje", "El archivo debe ser un CSV");
                return ResponseEntity.badRequest().body(respuesta);
            }
            
            // Procesar el archivo y reemplazar el grafo
            long startTime = System.currentTimeMillis();
            boolean procesado = grafoService.cargarGrafoDesdeArchivo(file);
            long endTime = System.currentTimeMillis();
            long tiempoProcesamiento = endTime - startTime;
            
            if (procesado) {
                // Obtener estadísticas del nuevo grafo
                List<ConexionGrafo> todasLasConexiones = grafoService.obtenerTodasLasConexiones();
                List<String> ubicaciones = grafoService.obtenerUbicacionesUnicas();
                
                respuesta.put("success", true);
                respuesta.put("mensaje", "Archivo procesado exitosamente");
                respuesta.put("nombreArchivo", nombreArchivo);
                respuesta.put("tamañoArchivo", file.getSize());
                respuesta.put("tiempoProcesamiento", tiempoProcesamiento + "ms");
                respuesta.put("totalConexiones", todasLasConexiones.size());
                respuesta.put("totalUbicaciones", ubicaciones.size());
                
                logger.info("Bulk upload completado: {} conexiones, {} ubicaciones, {}ms", 
                    todasLasConexiones.size(), ubicaciones.size(), tiempoProcesamiento);
                
                return ResponseEntity.ok(respuesta);
            } else {
                respuesta.put("success", false);
                respuesta.put("mensaje", "Error al procesar el archivo CSV");
                return ResponseEntity.badRequest().body(respuesta);
            }
            
        } catch (IOException e) {
            logger.error("Error al procesar el archivo CSV", e);
            respuesta.put("success", false);
            respuesta.put("mensaje", "Error interno al procesar el archivo: " + e.getMessage());
            return ResponseEntity.internalServerError().body(respuesta);
        } catch (Exception e) {
            logger.error("Error inesperado al procesar el archivo CSV", e);
            respuesta.put("success", false);
            respuesta.put("mensaje", "Error inesperado: " + e.getMessage());
            return ResponseEntity.internalServerError().body(respuesta);
        }
    }

}

