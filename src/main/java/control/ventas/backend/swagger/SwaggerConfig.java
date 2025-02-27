package control.ventas.backend.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI custonOpenAPI() {
		return new OpenAPI().info(new Info().title("API Sistema de Control de Ventas")
											.version("1.0.0")
											.description("Esta documentación es para el sistema de control de ventas gestionada con una base de datos de MongoDB Atlas"));
	}
}
