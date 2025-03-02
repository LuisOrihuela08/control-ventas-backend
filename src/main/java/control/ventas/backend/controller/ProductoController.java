package control.ventas.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import control.ventas.backend.dto.ProductoDTO;
import control.ventas.backend.entity.Producto;
import control.ventas.backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api-producto")
public class ProductoController {

	@Autowired
	private ProductoService productoService;
	
	@GetMapping("/list-all")
	public ResponseEntity<?> getAllProductos(){
		
		try {
			
			List<Producto> listAllProductos = productoService.findAllProductos();
			log.info("Listado de Productos OK");
			return new ResponseEntity<>(listAllProductos, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("ERROR AL LISTAR PRODUCTOS", e);
			return new ResponseEntity<>(Map.of("error", "Hubo un error al listar todos los productos", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Listado Producto con paginación
	@GetMapping("/list-producto-page")
	public ResponseEntity<?> listProductosPagination (@RequestParam ("page") int page, @RequestParam ("size") int size){
		
		try {
			Page <Producto> listProductosPage = productoService.findProductosByPagination(page, size);
			log.info("Productos Pagination OK");
			return new ResponseEntity<>(listProductosPage, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Producto Pagination ERROR", e);
			return new ResponseEntity<>(Map.of("error", "Error al listar los productos paginados", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}	 
	}
	
	@PostMapping("/register")
	public ResponseEntity <?> registerProducto(@RequestBody ProductoDTO productoDTO){
		
		try {
			
			Producto producto = new Producto();
			producto.setNombre_producto(productoDTO.getNombre_producto());
			producto.setCantidad(productoDTO.getCantidad());
			producto.setPrecio_unitario(productoDTO.getPrecio_unitario());
			producto.setMarca(productoDTO.getMarca());
			productoService.saveProducto(producto);
			
			log.info("Producto Registrado OK {}", producto);
			return new ResponseEntity<>(producto, HttpStatus.CREATED);
			
		} catch (Exception e) {
			log.error("registerProducto ERROR", e);
			return new ResponseEntity<>(Map.of("error", "Hubo un error al registrar el producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
