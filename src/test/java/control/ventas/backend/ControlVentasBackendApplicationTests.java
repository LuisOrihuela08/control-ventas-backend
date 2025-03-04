package control.ventas.backend;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import control.ventas.backend.entity.Producto;
import control.ventas.backend.entity.Venta;
import control.ventas.backend.repository.ProductoRepository;
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
	
	@Mock
	private ProductoRepository productoRepository;

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
			// Simulación del stock de productos en la BD antes de la venta
            Producto productoLaptop = new Producto("Laptop", 10, 35.0, "Lenovo");
            Producto productoParlantes = new Producto("Parlantes", 15, 35.0, "Lenovo");

            // Configurar Mockito para devolver los productos cuando se busquen en la BD
            Mockito.when(productoRepository.findByNombreProducto("Laptop")).thenReturn(productoLaptop);
            Mockito.when(productoRepository.findByNombreProducto("Parlantes")).thenReturn(productoParlantes);

            // Simulación de productos vendidos
            List<Producto> productosVendidos = Arrays.asList(
                new Producto("Laptop", 2, 35.0, "Lenovo"),    // Se vendieron 2
                new Producto("Parlantes", 3, 35.0, "Lenovo")  // Se vendieron 3
            );

            Venta venta = new Venta("1", productosVendidos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());

            // Simular el guardado de la venta
            Mockito.when(ventaRepository.save(Mockito.any(Venta.class))).thenReturn(venta);

            // Ejecutar el método de prueba
            Venta ventaResultado = ventaService.registerVenta(venta);

            log.info("Venta registrada: {}", ventaResultado);

            assertNotNull(ventaResultado);
            assertEquals(2, ventaResultado.getProductos_vendidos().size());
            assertEquals(1600.0, ventaResultado.getMonto_total());
            assertEquals("Efectivo", ventaResultado.getMetodo_pago());
            assertEquals(1700.0, ventaResultado.getDinero_cliente());
            assertEquals(100.0, ventaResultado.getVuelto());

            // Capturar los productos guardados en productoRepository
            ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
            Mockito.verify(productoRepository, Mockito.times(2)).save(productoCaptor.capture());

            List<Producto> productosGuardados = productoCaptor.getAllValues();

            // Validar los productos guardados
            Producto laptopGuardada = productosGuardados.stream()
                .filter(p -> p.getNombreProducto().equals("Laptop"))
                .findFirst()
                .orElse(null);

            Producto parlantesGuardados = productosGuardados.stream()
                .filter(p -> p.getNombreProducto().equals("Parlantes"))
                .findFirst()
                .orElse(null);

            // Verificar que los productos fueron actualizados con el stock correcto
            assertNotNull(laptopGuardada);
            assertEquals(8, laptopGuardada.getCantidad()); // 10 - 2 vendidos

            assertNotNull(parlantesGuardados);
            assertEquals(12, parlantesGuardados.getCantidad()); // 15 - 3 vendidos

            // Capturar la venta guardada en ventaRepository
            ArgumentCaptor<Venta> ventaCaptor = ArgumentCaptor.forClass(Venta.class);
            Mockito.verify(ventaRepository, Mockito.times(1)).save(ventaCaptor.capture());

            Venta ventaGuardada = ventaCaptor.getValue();
            assertNotNull(ventaGuardada);
            assertEquals(2, ventaGuardada.getProductos_vendidos().size());

        } catch (Exception e) {
            log.error("ERROR TEST AGREGAR VENTA", e.getMessage(), e);
            fail("Se produjo un error en la prueba: " + e.getMessage());
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
			Producto productoLaptop = new Producto("Laptop", 10, 35.0, "Lenovo");
	        Producto productoParlantes = new Producto("Parlantes", 15, 35.0, "Lenovo");
	        
	        Mockito.when(productoRepository.findByNombreProducto("Laptop")).thenReturn(productoLaptop);
	        Mockito.when(productoRepository.findByNombreProducto("Parlantes")).thenReturn(productoParlantes);
	        
	        List<Producto> productos = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"),
					new Producto("Parlantes", 15, 35.0, "Lenovo"));

			Venta ventaExistente = new Venta("1", productos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());
			
			/*
			//Simulacion de edicion de los productos
			List<Producto> productosEditados = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"),
															 new Producto("Parlantes", 15, 35.0, "Genious"));
			
	       */
			
			Venta ventaEditada = new Venta("1", productos, 3000.0, "Tarjeta", 1700.0, 0.0, LocalDateTime.now());
						
			//Simulamos la actualizacion
			Mockito.when(ventaRepository.save(Mockito.any(Venta.class))).thenReturn(ventaEditada);
			
			Venta ventaResultado = ventaService.registerVenta(ventaEditada);
			
			log.info("Venta editada {}", ventaResultado);
			
			assertNotNull(ventaResultado);
			assertEquals(2, ventaResultado.getProductos_vendidos().size());
			assertEquals(3000.0, ventaResultado.getMonto_total());
			assertEquals("Tarjeta", ventaResultado.getMetodo_pago());
			assertEquals(1700.0, ventaResultado.getDinero_cliente());
			assertEquals(0.0, ventaResultado.getVuelto());
			
			//Verificamos ahora que se llama al metodo findById() y save()
			Mockito.verify(ventaRepository, Mockito.times(1)).save(Mockito.any(Venta.class));
			
		} catch (Exception e) {
			log.error("ERROR EN EL TEST EDITAR VENTA", e.getMessage(), e);
		}	
	}
	
	//Test para encontrar una Venta por su ID
	@Test
	public void testVentaFindById(){
		
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
	
	//Test para eliminar una venta
	@Test
	public void testEliminarVenta() {
		
		try {
			
			List<Producto> productoTest = Arrays.asList(new Producto("Laptop", 5, 35.0, "Lenovo")) ;
			
			Venta ventaTest = new Venta("1", productoTest, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());
			
			//Y aca hacemos la simulacion que lo eliminamos
			Mockito.doNothing().when(ventaRepository).deleteById("1");
			
			ventaService.deleteVenta("1");
			
			log.info("VENTA ELIMINADA OK {}", ventaTest);
			
			// Verificando que el método deleteById fue llamado con el ID correcto
	        Mockito.verify(ventaRepository, Mockito.times(1)).deleteById("1");
			
		} catch (Exception e) {
			log.error("ERROR AL ELIMINA LA VENTA ", e.getMessage(), e);
		}
	}
	
	
}
