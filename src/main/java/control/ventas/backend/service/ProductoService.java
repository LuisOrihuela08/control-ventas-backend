package control.ventas.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import control.ventas.backend.entity.Producto;
import control.ventas.backend.repository.ProductoRepository;

@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;
	
	public List<Producto> findAllProductos(){
		return productoRepository.findAll();
	}
	
	//Listar con productos con paginacion
	public Page<Producto> findProductosByPagination (int page, int size){
		Pageable pageable = PageRequest.of(page, size);
		return productoRepository.findAll(pageable);
	}
	
	public Producto saveProducto(Producto producto) {
		return productoRepository.save(producto);
	}
	
	public Optional<Producto>  findProductoById(String id) {
		return productoRepository.findById(id);
	}
	
	public void deleteProducto(String id) {
		productoRepository.deleteById(id);
	}
}
