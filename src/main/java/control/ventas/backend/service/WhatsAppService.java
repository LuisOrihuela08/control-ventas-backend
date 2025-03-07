package control.ventas.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WhatsAppService {

	@Value("${whatsapp.api-url}")
	private String whatsappApiUrl;
	
	@Value("${whatsapp.access-token}")
	private String accessToken;
	
	@Value("${whatsapp.recipient-phone}")
	private String recipientPhone;

	private final RestTemplate restTemplate;

	public WhatsAppService() {
		this.restTemplate = new RestTemplate();
	}
	
	public void sendWhatsAppMessage(String total, String metodoPago, String fecha, String productos) {
		
		try {
			// Limpiar los parámetros
            total = cleanText(total);
            metodoPago = cleanText(metodoPago);
            fecha = cleanText(fecha);
            productos = cleanText(productos);
            
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Bearer " + accessToken);
			
			Map<String, Object> body = Map.of(
		            "messaging_product", "whatsapp",
		            "to", recipientPhone,
		            "type", "template",
		            "template", Map.of(
		                "name", "control_wsp", // Reemplaza con el nombre real del template aprobado
		                "language", Map.of("code", "es_PE"), // Asegúrate de que sea el idioma correcto
		                "components", List.of(
		                    Map.of(
		                        "type", "body",
		                        "parameters", List.of(
		                            Map.of("type", "text", "text", total),
		                            Map.of("type", "text", "text", metodoPago),
		                            Map.of("type", "text", "text", fecha),
		                            Map.of("type", "text", "text", productos)
		                        )
		                    )
		                )
		            )
		        );
					
			HttpEntity<String> request = new HttpEntity<>(new ObjectMapper().writeValueAsString(body), headers);
            ResponseEntity<String> response = restTemplate.exchange(whatsappApiUrl, HttpMethod.POST, request, String.class);
			
            log.info("Respuesta de WhatsApp API:  {}", response.getBody());
            log.info("Mensaje {}", body);
            
		} catch (Exception e) {
			log.error("Error al enviar el mensaje de WhatsApp", e);
		}
	}
	
	// Método para limpiar los textos
    private String cleanText(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("[\\n\\t]+", " ") // Reemplazar nueva línea y tabulaciones con un espacio
                   .replaceAll(" {2,}", " ")      // Reemplazar múltiples espacios con un solo espacio
                   .trim();                       // Eliminar espacios en los extremos
    }

	public String enviarMensajePrueba(String numeroTelefono) {
        RestTemplate restTemplate = new RestTemplate();

        // Configurar encabezados
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Crear cuerpo del mensaje
        Map<String, Object> message = new HashMap<>();
        message.put("messaging_product", "whatsapp");
        message.put("to", numeroTelefono);
        message.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", "hello_world");

        Map<String, String> language = new HashMap<>();
        language.put("code", "en_US");

        template.put("language", language);
        message.put("template", template);

        // Enviar petición
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(message, headers);
        return restTemplate.postForObject(whatsappApiUrl, request, String.class);
    }
}
