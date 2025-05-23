package control.ventas.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import control.ventas.backend.dto.ReporteVentasPorMetodoPagoDTO;
import control.ventas.backend.entity.Venta;

public interface VentaRepository extends MongoRepository<Venta, String>{

	//Método para filtro ventas por fecha
	Page<Venta> findVentaByFechaCompraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

	//Método para filtrar ventas entre fechas, para reporte de PDF
	List<Venta> findByFechaCompraBetween(LocalDateTime  fechaInicio, LocalDateTime fechaFin);
	
	//Paginar las ventas
	Page<Venta> findAll(Pageable pageable);
	
	//Método para buscar ventas por metodo de pago
	Page<Venta> findVentaByMetodoPago(String metodoPago, Pageable pageable);
	
	//Método para encontrar una venta por el nombre del producto
	@Query("{ 'productos_vendidos.nombre_producto': ?0 }")
	Page<Venta> findByNombreProducto(String nombreProducto, Pageable pageable);
	
	//Método para calcular las ventas segun el metodo de pago
	@Aggregation(pipeline = {
		    "{ $group: { _id: '$metodo_pago', totalVentas: { $sum: '$monto_total' }, cantidadVentas: { $sum: 1 } } }"
		})
		List<ReporteVentasPorMetodoPagoDTO> obtenerTotalVentasPorMetodoPago();

}
