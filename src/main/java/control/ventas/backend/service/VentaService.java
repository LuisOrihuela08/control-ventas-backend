package control.ventas.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import control.ventas.backend.dto.ReporteVentasPorMetodoPagoDTO;
import control.ventas.backend.entity.Venta;
import control.ventas.backend.repository.VentaRepository;

@Service
public class VentaService {

	@Autowired
	private VentaRepository ventaRepository;
	
	public List<Venta> findAllVentas(){
		return ventaRepository.findAll();
	}
	
	public Venta registerVenta(Venta venta) {
		return ventaRepository.save(venta);
	}
	
	public Venta findVentaById(String id) {
		return ventaRepository.findById(id).orElse(null);
	}
	
	public void deleteVenta(String id) {
		ventaRepository.deleteById(id);
	}
	
	//Método para buscar ventas entre fechas
	public List<Venta> findVentasBetweenFecha(String fechaInicioString, String fechaFinString){
		LocalDateTime fechaInicio = LocalDate.parse(fechaInicioString).atStartOfDay();
        LocalDateTime fechaFin = LocalDate.parse(fechaFinString).plusDays(1).atStartOfDay();

        // Realizar la consulta con el rango de fechas
        return ventaRepository.findVentaByFechaCompraBetween(fechaInicio, fechaFin);
	}
	
	//Método para paginas las ventas
	public Page<Venta> listVentaByPages(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return ventaRepository.findAll(pageable);
	}
	
	//Método para buscar una venta por el nombre del producto
	public List<Venta> findVentaByNombreProducto(String nombreProducto){
		return ventaRepository.findByNombreProducto(nombreProducto);
	}
	
	//Método para obtener el total de ventas por el metodo de pago
	public List<ReporteVentasPorMetodoPagoDTO> findVentasByMetodoPago(){
		return ventaRepository.obtenerTotalVentasPorMetodoPago();
	}
}
