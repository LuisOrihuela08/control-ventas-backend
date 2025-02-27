package control.ventas.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import control.ventas.backend.entity.Venta;

public interface VentaRepository extends MongoRepository<Venta, String>{

}
