package control.ventas.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
