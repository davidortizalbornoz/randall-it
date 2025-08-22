package cl.randall.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConexionGrafo {
    
    private String locStart;
    private String locEnd;
    private Integer time;
}
