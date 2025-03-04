package control.ventas.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
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

@Service
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
