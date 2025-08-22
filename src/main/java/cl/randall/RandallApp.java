package cl.randall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
@ServletComponentScan
public class RandallApp {
    
    private static final Logger logger = LoggerFactory.getLogger(RandallApp.class);
    
    public static void main(String[] args) {
        SpringApplication.run(RandallApp.class, args);
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void logSwaggerUrl() {
        logger.info("🚀 Randall IT API iniciada exitosamente!");
        logger.info("📚 Swagger UI disponible en: http://localhost:8080/swagger-ui/index.html");
        logger.info("📖 OpenAPI JSON disponible en: http://localhost:8080/v3/api-docs");
        logger.info("🔗 Endpoints disponibles:");
        logger.info("   - GET  /grafo/estado");
        logger.info("   - GET  /grafo/tiempo/{origen}/{destino}");
        logger.info("   - POST /bulk-upload");
    }
}
