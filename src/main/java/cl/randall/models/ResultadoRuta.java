package cl.randall.models;

import java.util.List;

/**
 * Clase para representar el resultado de una ruta
 */
public class ResultadoRuta {
    private final List<String> ruta;
    private final int tiempoTotal;
    
    public ResultadoRuta(List<String> ruta, int tiempoTotal) {
        this.ruta = ruta;
        this.tiempoTotal = tiempoTotal;
    }
    
    public List<String> getRuta() { return ruta; }
    public int getTiempoTotal() { return tiempoTotal; }
}
