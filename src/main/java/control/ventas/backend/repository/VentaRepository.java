package control.ventas.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import control.ventas.backend.entity.Venta;

public interface VentaRepository extends MongoRepository<Venta, String>{

	//Método para filtro ventas por fecha
	List<Venta> findVentaByFechaCompraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

	//Paginar las ventas
	Page<Venta> findAll(Pageable pageable);
}
