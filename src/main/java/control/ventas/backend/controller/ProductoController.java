package control.ventas.backend.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import control.ventas.backend.entity.Producto;
import control.ventas.backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api-producto")
@Slf4j
public class ProductoController {

	@Autowired
	private ProductoService productoService;
	
	@GetMapping("/list")
	public ResponseEntity<?> getAllProducts(){
		
		try {
			
			List<Producto> listProducts = productoService.findAllProductos();
			log.info("Listado de productos OK");
			return new ResponseEntity<>(listProducts, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("List Error", e);
			return new ResponseEntity<>(Map.of("error", "Error al obtener la lista de productos", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerProduct (@RequestBody Producto producto){
		
		try {
			
			Producto productoNuevo = productoService.saveProducto(producto);
			log.info("Producto agregado {}", productoNuevo);
			return new ResponseEntity<>(productoNuevo, HttpStatus.CREATED);
			
			
		} catch (Exception e) {
			log.error("Register product ERROR", e);
			return new ResponseEntity<>(Map.of("error", "Error al registrar nuevo producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateProduct(@PathVariable("id") String id, @RequestBody Producto producto){
		
		try {
			
			Optional<Producto> productEncontrado = productoService.findProductoById(id);
			
			if (productEncontrado.isEmpty()) {
				return new ResponseEntity<>(Map.of("error","Error al encontrar el producto"), HttpStatus.NOT_FOUND);				
			}
			
			Producto productoExistente = productEncontrado.get();
			productoExistente.setNombre(producto.getNombre());
			productoExistente.setPrecio_unitario(producto.getPrecio_unitario());
			productoExistente.setStock(producto.getStock());
			
			Producto productoActualizado = productoService.saveProducto(productoExistente);
			
			log.info("Producto Actualizado: {}", productoActualizado);
			return new ResponseEntity<>(productoActualizado, HttpStatus.OK);	
			
		} catch (Exception e) {
			log.error("Update Producto ERROR", e);
			return new ResponseEntity<>(Map.of("error", "Error al actualizar el producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable ("id") String id){
		
		try {
			
			Optional<Producto> productoEncontrado = productoService.findProductoById(id);
			
			if (productoEncontrado.isEmpty()) {
				log.error("No se encontro el producto, ERROR");
				return new ResponseEntity<>(Map.of("error", "Error al encontrar el producto con el id: " + id), HttpStatus.NOT_FOUND);
			}
			
			log.info("Producto encontrado{}", productoEncontrado);
			return new ResponseEntity<>(productoEncontrado, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Hubo un error", e);
			return new ResponseEntity<>(Map.of("error", "Error al encontrar el producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteProduct (@PathVariable ("id") String id){
		
		try {
			
			Optional<Producto> productoEncontrado = productoService.findProductoById(id);
			
			if (productoEncontrado.isEmpty()) {
				log.error("No se encontro el producto con el id: " + id);
				return new ResponseEntity<>(Map.of("error", "Error al encontrar el producto con el id: " + id), HttpStatus.NOT_FOUND);
			}
			productoService.deleteProducto(id);
			log.info("Producto eliminado con el id: " + id);
			return new ResponseEntity<>(Map.of("detalle", "Producto eliminado correctamente"), HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("ERROR product delete",e);
			return new ResponseEntity<>(Map.of("detalle", "Se pudo eliminar el producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
