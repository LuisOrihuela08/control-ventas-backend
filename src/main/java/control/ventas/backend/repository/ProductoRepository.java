package control.ventas.backend.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import control.ventas.backend.entity.Producto;

public interface ProductoRepository extends MongoRepository<Producto, String>{

	//Listar Productos por paginacion
	Page<Producto> findAll (Pageable pageable);
	
	//Método para buscar por nombre para registrar una venta
	Producto findByNombreProducto(String nombre_producto);
	
	//Método para buscar producto por codigo
	Producto findByCodigo(String codigo);
	
	//Método para filtrar producto por nombre, este se usara en el frontend como filtro
	Page<Producto> findProductoByNombreProducto(String nombreProducto, Pageable pageable);
	
	//Método para filtrar producto por nombre pero me devuelve una lista, para buscar desde en el frontend
	List<Producto> findVentaByNombreProducto(String nombreProducto);
	
	//Método para filtrar producto por marca
	Page<Producto> findProductoByMarca (String marca, Pageable pageable);
	
	//Esto es para verificar si existe o no el producto por el nombre
	//Lo utilizo para importar el excel
	boolean existsByNombreProducto(String nombreProducto);
	
	//Esto es para importar excel, pero para verificar si el producto existe por nombre
	//y actualizar los registros
	Optional<Producto> getByNombreProducto (String nombreProducto);
}
