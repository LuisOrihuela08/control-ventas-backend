package control.ventas.backend.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import control.ventas.backend.entity.Venta;
import control.ventas.backend.service.VentaService;

@RestController
@RequestMapping("/api-venta")
public class VentaController {

	@Autowired
	private VentaService ventaService;

	private final Logger logger = LoggerFactory.getLogger(VentaController.class);

	@GetMapping("/list")
	public ResponseEntity<?> getAllVentas() {

		try {

			List<Venta> listVentas = ventaService.findAllVentas();
			logger.info("Listado de las ventas OK");
			return new ResponseEntity<>(listVentas, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error al listar las Ventas", e);
			return new ResponseEntity<>(Map.of("error", "Error al listar las ventas", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerVenta(@RequestBody Venta venta) {

		try {

			Venta ventaNueva = ventaService.registerVenta(venta);
			logger.info("Venta creada {}", ventaNueva);
			return new ResponseEntity<>(ventaNueva, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Error al crear la venta", e);
			return new ResponseEntity<>(Map.of("error", "Error al crear la venta", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/find-venta/{id}")
	public ResponseEntity<?> getVentaById(@PathVariable("id") String id) {

		try {

			Venta ventaEncontrada = ventaService.findVentaById(id);
			
			if (ventaEncontrada == null) {
				logger.error("Venta no encontrada en la base de datos");
				return new ResponseEntity<>("Error al encontrar la venta", HttpStatus.BAD_REQUEST);
			}
			
			logger.info("Venta encontrada {}", ventaEncontrada);
			return new ResponseEntity<>(ventaEncontrada, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error al encontrar la venta {}", e);
			return new ResponseEntity<>(Map.of("error", "Error al encontrar la venta", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
