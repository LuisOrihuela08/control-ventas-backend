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
import control.ventas.backend.entity.Producto;
import control.ventas.backend.entity.Venta;
import control.ventas.backend.repository.ProductoRepository;
import control.ventas.backend.repository.VentaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VentaService {

	@Autowired
	private VentaRepository ventaRepository;
	
	@Autowired
	private ProductoRepository productoRepository;
	
	public List<Venta> findAllVentas(){
		return ventaRepository.findAll();
	}
	
	public Venta registerVenta(Venta venta) {
		
		try {
			
			//Antes de guardar una venta, voy a verificar si el producto tiene stock
			for(Producto productoVendido: venta.getProductos_vendidos()) {
				Producto productoBD = productoRepository.findByNombreProducto(productoVendido.getNombreProducto());
				
				if (productoBD == null) {
					throw new IllegalArgumentException("El producto: " + productoVendido.getNombreProducto() + " no existe");
				}
				
				if (productoBD.getCantidad() < productoVendido.getCantidad()) {
					throw new IllegalArgumentException("Stock insuficiente para el producto " + productoVendido.getNombreProducto());
				}
			}
			
			//Y aca restamos el stock y actualizamos los productos
			for(Producto productoVendido: venta.getProductos_vendidos()) {
				Producto productoBD = productoRepository.findByNombreProducto(productoVendido.getNombreProducto());
				productoBD.setCantidad(productoBD.getCantidad() - productoVendido.getCantidad());
				productoRepository.save(productoBD);
			}
			
			return ventaRepository.save(venta);
			
		} catch (Exception e) {
			throw e;
		}	
		
	}
	
	public Venta findVentaById(String id) {
		return ventaRepository.findById(id).orElse(null);
	}
	
	public void deleteVenta(String id) {
		ventaRepository.deleteById(id);
	}
	
	//Método para buscar ventas entre fechas
	public Page<Venta> findVentasBetweenFecha(String fechaInicioString, String fechaFinString, int page, int size){
		
		try {
			Pageable pageable = PageRequest.of(page, size);
			LocalDateTime fechaInicio = LocalDate.parse(fechaInicioString).atStartOfDay();
	        LocalDateTime fechaFin = LocalDate.parse(fechaFinString).plusDays(1).atStartOfDay();

	        // Realizar la consulta con el rango de fechas
	        return ventaRepository.findVentaByFechaCompraBetween(fechaInicio, fechaFin, pageable);
	        
		} catch (Exception e) {
			log.error("Error al buscar ventas por fechas: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar ventas por fechas: " + e.getMessage());
		}
		
	}
	
	//Método para paginar las ventas
	public Page<Venta> listVentaByPages(int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return ventaRepository.findAll(pageable);
	}
	
	//Método para buscar una venta por el nombre del producto
	public Page<Venta> findVentaByNombreProducto(String nombreProducto, int page, int size){
		try {
			Pageable pageable = PageRequest.of(page, size);
			return ventaRepository.findByNombreProducto(nombreProducto, pageable);
		} catch (Exception e) {
			log.error("Error al buscar ventas por nombre del producto: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por ventas por el nombre del producto: " + e.getMessage());
		}	
	}
	
	
	//Método para obtener el total de ventas por el metodo de pago
	public List<ReporteVentasPorMetodoPagoDTO> findVentasByMetodoPago(){
		return ventaRepository.obtenerTotalVentasPorMetodoPago();
	}
	
	//Método para filtrar ventas por metodo de pago
	public Page<Venta> findVentaByMetodoPago(String metodoPago, int page, int size){
		
		try {
			
			Pageable pageable = PageRequest.of(page, size);
			return ventaRepository.findVentaByMetodoPago(metodoPago, pageable);
			
		} catch (Exception e) {
			log.error("Error al buscar ventas por método de pago: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por ventas por método de pago: " + e.getMessage());
		}
	}
}
