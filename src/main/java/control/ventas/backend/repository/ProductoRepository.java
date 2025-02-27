package control.ventas.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import control.ventas.backend.entity.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String>{

}
