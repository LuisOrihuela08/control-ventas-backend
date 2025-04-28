package control.ventas.backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import control.ventas.backend.entity.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String>{

	//Listar Productos por paginacion
	Page<Producto> findAll (Pageable pageable);
	
	//Método para buscar por nombre para registrar una venta
	Producto findByNombreProducto(String nombre_producto);
	
	//Método para filtrar producto por nombre, este se usara en el frontend como filtro
	Page<Producto> findProductoByNombreProducto(String nombreProducto, Pageable pageable);
	
	//Método para filtrar producto por marca
	Page<Producto> findProductoByMarca (String marca, Pageable pageable);
}
