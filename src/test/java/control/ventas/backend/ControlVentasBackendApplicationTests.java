package control.ventas.backend;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import control.ventas.backend.entity.Producto;
import control.ventas.backend.entity.Venta;
import control.ventas.backend.repository.VentaRepository;
import control.ventas.backend.service.VentaService;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Slf4j
class ControlVentasBackendApplicationTests {

	@Mock
	private VentaRepository ventaRepository;

	@InjectMocks
	private VentaService ventaService;

	// @Test
	// void contextLoads() {
	// }

	// Esto es para configurar pruebas unitarias con JUnit y Mockito

	// Test para registrar una venta
	@Test
	public void testRegistrarVenta() {

		try {
			
			// Simulacion de registros
			List<Producto> productos = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"),
					new Producto("Parlantes", 15, 35.0, "Lenovo"));

			Venta venta = new Venta("1", productos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());

			// Esto simula el comportamieto del repositorio
			Mockito.when(ventaRepository.save(Mockito.any(Venta.class))).thenReturn(venta);

			// Activar logs de Mockito
			System.setProperty("org.mockito.logging.level", "DEBUG");

			Venta ventaResultado = ventaService.registerVenta(venta);

			log.info("Venta registrada {}", ventaResultado);

			// Verificar que la venta no sea nula
			assertNotNull(ventaResultado);

			// Validar los atributos
			assertEquals(2, ventaResultado.getProductos_vendidos().size());
			assertEquals(1600.0, ventaResultado.getMonto_total());
			assertEquals("Efectivo", ventaResultado.getMetodo_pago());
			assertEquals(1700.0, ventaResultado.getDinero_cliente()); // Corregido
			assertEquals(100.0, ventaResultado.getVuelto());

			// Verificar que se haya llamado al método `save()`
			Mockito.verify(ventaRepository, Mockito.times(1)).save(venta);
			
		} catch (Exception e) {
			log.error("ERROR TEST AGREGAR VENTA", e.getMessage(), e);
		}
		

	}

	// Test para listar las ventas
	@Test
	public void testListarVentas() {
		
		try {
			
			List<Producto> producto1 = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"));
			List<Producto> producto2 = Arrays.asList(new Producto("Parlantes", 15, 35.0, "Lenovo"));

			Venta venta1 = new Venta("1", producto1, 3000.0, "Efectivo", 3100.0, 100.0, LocalDateTime.now());
			Venta venta2 = new Venta("2", producto2, 250.0, "Tarjeta", 250.0, 0.0, LocalDateTime.now());

			List<Venta> listVentasTest = Arrays.asList(venta1, venta2);

			Mockito.when(ventaRepository.findAll()).thenReturn(listVentasTest);

			List<Venta> resultadoTestList = ventaService.findAllVentas();

			System.setProperty("org.mockito.logging.level", "DEBUG");

			log.info("Lista de ventas {}", resultadoTestList);

			// Validaciones
			assertNotNull(resultadoTestList);
			assertEquals(2, resultadoTestList.size());
			assertEquals("Efectivo", resultadoTestList.get(0).getMetodo_pago());
			assertEquals("Tarjeta", resultadoTestList.get(1).getMetodo_pago());

			// Verificar que se llamó a findAll() exactamente una vez
			Mockito.verify(ventaRepository, Mockito.times(1)).findAll();
			
		} catch (Exception e) {
			log.error("ERROR EN EL TEST LISTAR VENTA", e.getMessage(), e);
		}

		
	}

	// Test para editar una venta
	@Test
	public void testEditarVentas() {
		
		try {
			
			// Simulacion de registros
			List<Producto> productos = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"),
													 new Producto("Parlantes", 15, 35.0, "Lenovo"));

			Venta ventaExistente = new Venta("1", productos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());
			
			//Simulacion de edicion de los productos
			List<Producto> productosEditados = Arrays.asList(new Producto("Laptop", 5, 35.0, "Lenovo"),
															 new Producto("Parlantes", 15, 35.0, "Genious"));
			
			Venta ventaEditada = new Venta("1", productosEditados, 3000.0, "Tarjeta", 3100.0, 0.0, LocalDateTime.now());
						
			//Simulamos la actualizacion
			Mockito.when(ventaRepository.save(Mockito.any(Venta.class))).thenReturn(ventaEditada);
			
			Venta ventaResultado = ventaService.registerVenta(ventaEditada);
			
			log.info("Venta editada {}", ventaResultado);
			
			assertNotNull(ventaResultado);
			assertEquals(2, ventaResultado.getProductos_vendidos().size());
			assertEquals(3000.0, ventaResultado.getMonto_total());
			assertEquals("Tarjeta", ventaResultado.getMetodo_pago());
			assertEquals(3100.0, ventaResultado.getDinero_cliente());
			assertEquals(0.0, ventaResultado.getVuelto());
			
			//Verificamos ahora que se llama al metodo findById() y save()
			Mockito.verify(ventaRepository, Mockito.times(1)).save(Mockito.any(Venta.class));
			
		} catch (Exception e) {
			log.error("ERROR EN EL TEST EDITAR VENTA", e.getMessage(), e);
		}	
	}
	
	//Test para encontrar una Venta por su ID
	@Test
	public void ventaFindById(){
		
		try {
			
			List<Producto> productos = Arrays.asList(new Producto("Laptop", 5, 35.0, "Lenovo"),
													 new Producto("Parlantes", 15, 35.0, "Genious"));
			
			Optional<Venta> venta = Optional.ofNullable(new Venta("1", productos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now()));
			
			Mockito.when(ventaRepository.findById("1")).thenReturn(venta);
			
			Venta ventaEncontrada = ventaService.findVentaById("1");
			
			log.info("Venta Encontrada {}", ventaEncontrada);
			
			assertNotNull(ventaEncontrada);
			assertEquals(2, ventaEncontrada.getProductos_vendidos().size());
			assertEquals(1600.0, ventaEncontrada.getMonto_total());
			assertEquals("Efectivo", ventaEncontrada.getMetodo_pago());
			assertEquals(1700.0, ventaEncontrada.getDinero_cliente());
			assertEquals(100.0, ventaEncontrada.getVuelto());
			
			Mockito.verify(ventaRepository, Mockito.times(1)).findById("1");
			
		} catch (Exception e) {
			log.error("ERROR EN EL TEST VENTA FIND BY ID", e.getMessage(), e);
		}
	}
}
