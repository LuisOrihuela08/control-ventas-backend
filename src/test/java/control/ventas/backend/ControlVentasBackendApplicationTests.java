package control.ventas.backend;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ControlVentasBackendApplicationTests {

	@Mock
	private VentaRepository ventaRepository;

	@InjectMocks
	private VentaService ventaService;

	
	//@Test
	//void contextLoads() {
	//}
	
	//Esto es para configurar pruebas unitarias con JUnit y Mockito
	
	//Test para registrar una venta
	@Test
	public void testRegistrarVenta() {

		//Simulacion de registros
		List<Producto> productos = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"),
												 new Producto("Parlantes", 15, 35.0, "Lenovo"));

		Venta venta = new Venta("1", productos, 1600.0, "Efectivo", 1700.0, 100.0, LocalDateTime.now());
		
		//Esto simula el comportamieto del repositorio
		Mockito.when(ventaRepository.save(Mockito.any(Venta.class))).thenReturn(venta);
		
		// Activar logs de Mockito
	    System.setProperty("org.mockito.logging.level", "DEBUG");

		
		Venta ventaResultado = ventaService.registerVenta(venta);
		
		System.out.println("Venta registrada: " + ventaResultado);
		
		// Verificar que la venta no sea nula
        assertNotNull(ventaResultado);

        // Validar los atributos
        assertEquals(2, ventaResultado.getProductos_vendidos().size());
        assertEquals(1600.0, ventaResultado.getMonto_total());
        assertEquals("Efectivo", ventaResultado.getMetodo_pago());
        assertEquals(1700.0, ventaResultado.getDinero_cliente());  // Corregido
        assertEquals(100.0, ventaResultado.getVuelto());
        
     // Verificar que se haya llamado al método `save()`
        Mockito.verify(ventaRepository, Mockito.times(1)).save(venta);
        
	}	
	
	//Test para listar las ventas
	@Test
	public void testListarVentas() {
		
		List<Producto> producto1 = Arrays.asList(new Producto("Laptop", 10, 35.0, "Lenovo"));
		List<Producto> producto2 = Arrays.asList(new Producto("Parlantes", 15, 35.0, "Lenovo"));
		
		Venta venta1 = new Venta("1", producto1, 3000.0, "Efectivo", 3100.0, 100.0, LocalDateTime.now());
		Venta venta2 = new Venta("2", producto2, 250.0, "Tarjeta", 250.0, 0.0, LocalDateTime.now());
		
		List<Venta> listVentasTest = Arrays.asList(venta1, venta2);
		
		Mockito.when(ventaRepository.findAll()).thenReturn(listVentasTest);
		
		List<Venta> resultadoTestList = ventaService.findAllVentas();
				
		System.setProperty("org.mockito.logging.level", "DEBUG");
		
		System.out.println("Lista de las ventas: " + resultadoTestList);
		
		// Validaciones
	    assertNotNull(resultadoTestList);
	    assertEquals(2, resultadoTestList.size());
	    assertEquals("Efectivo", resultadoTestList.get(0).getMetodo_pago());
	    assertEquals("Tarjeta", resultadoTestList.get(1).getMetodo_pago());

	    // Verificar que se llamó a findAll() exactamente una vez
	    Mockito.verify(ventaRepository, Mockito.times(1)).findAll();
	}
	
	
}
