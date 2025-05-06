package control.ventas.backend.controller;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import control.ventas.backend.dto.ProductoDTO;
import control.ventas.backend.entity.Producto;
import control.ventas.backend.service.ProductoService;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/producto")
@CrossOrigin(origins = "http://localhost:4200/", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,RequestMethod.DELETE, RequestMethod.OPTIONS })//Esta URL es para pruebas de manera local
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
	@GetMapping("/list-page")
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
	
	//Método para buscar producto por nombre
	@GetMapping("/find-nombre/{nombreProducto}")
	public ResponseEntity<?> getProductoByNombre (@PathVariable ("nombreProducto") String nombreProducto, 
												  @RequestParam ("page") int page,
												  @RequestParam ("size") int size){
		
		try {
			
			if (nombreProducto.isBlank()) {
				log.error("Error, el nombre no puede estar vacío para la búsqueda");
				return new ResponseEntity<>(Map.of("mensaje", "Error, el nombre no puede estar vacío o en blanco"), HttpStatus.BAD_REQUEST);
			}
			
			Page<Producto> productoPage = productoService.findProductoByNombre(nombreProducto, page, size);
			
			if (productoPage.isEmpty()) {
				log.error("No existe producto con el nombre: {}", nombreProducto);
				return new ResponseEntity<>(Map.of("mensaje", "No existe producto con el nombre: " + nombreProducto), HttpStatus.NOT_FOUND);
			}
			
			log.info("Filtro por nombre del producto");
			log.info("Búsqueda exitosa con el nombre: {}", nombreProducto);
			return new ResponseEntity<>(productoPage, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Hubo un error al buscar producto con el nombre: {}", nombreProducto);
			return new ResponseEntity<>(Map.of("error", "Error, al buscar el producto con el nombre: "+ nombreProducto,
											   "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//Método para buscar producto por nombre, y es consumido desde el componente de agregar venta desde el Frontend
	@GetMapping("/find/nombreProducto/venta/{nombreProducto}")
	public ResponseEntity<?> getProductoByNombreForVenta(@PathVariable ("nombreProducto") String nombreProducto){
		
		try {
			
			if (nombreProducto.isBlank()) {
				log.error("Error, el nombre no puede estar vacío para la búsqueda");
				return new ResponseEntity<>(Map.of("mensaje", "Error, el nombre no puede estar vacío o en blanco"), HttpStatus.BAD_REQUEST);
			}
			
			List<Producto> listProductos = productoService.findByNombreProductoForVenta(nombreProducto);
			
			if (listProductos.isEmpty()) {
				log.error("No existe producto con el nombre: {}", nombreProducto);
				return new ResponseEntity<>(Map.of("mensaje", "No existe producto con el nombre: " + nombreProducto), HttpStatus.NOT_FOUND);
			}
			
			log.info("Búsqueda exitosa con el nombre: {}", nombreProducto);
			log.info("Producto(s) encontrado(s): {}", listProductos);
			return new ResponseEntity<>(listProductos, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Hubo un error al buscar producto con el nombre: {}", nombreProducto);
			return new ResponseEntity<>(Map.of("error", "Error, al buscar el producto con el nombre: "+ nombreProducto,
											   "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	//Método para buscar producto por la marca
	@GetMapping("/find-marca/{marca}")
	public ResponseEntity<?> getProductoByMarca (@PathVariable ("marca") String marca,
												 @RequestParam ("page") int page,
												 @RequestParam ("size") int size){
		
		try {
			
			if (marca.isBlank()) {
				log.error("Error, la marca no puede estar vacia");
				return new ResponseEntity<>(Map.of("mensaje", "Error, la marca no puede estar vacia para la búsqueda"), HttpStatus.BAD_REQUEST);
			}
			
			Page<Producto> productoPage = productoService.findProductoByMarca(marca, page, size);
			
			if (productoPage.isEmpty()) {
				log.error("No existe producto con la marca: {}", marca);
				return new ResponseEntity<>(Map.of("mensaje", "Error, no existe producto con la marca: " + marca), HttpStatus.NOT_FOUND);
			}
			
			log.info("Filtro por marca del producto");
			log.info("Búsqueda exitosa con la marca: {}", marca);
			return new ResponseEntity<>(productoPage, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error, no se logró filtrar el producto con la marca: {}", marca, e);
			return new ResponseEntity<>(Map.of("error", "Error, no se logró filtrar los productos con la marca: "+ marca,
											   "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@PostMapping("/register")
	public ResponseEntity <?> registerProducto(@RequestBody ProductoDTO productoDTO){
		
		try {
			
			Producto producto = new Producto();
			producto.setNombreProducto(productoDTO.getNombreProducto());
			producto.setCodigo(productoDTO.getCodigo());
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
	
	@PutMapping("/update/{id}")
	public ResponseEntity<?> updateProducto (@PathVariable ("id") String id,
											 @RequestBody ProductoDTO productoDTO){
		
		try {
			
			Optional<Producto> productoOptional = productoService.findProductoById(id);
			
			if (productoOptional.isEmpty()) {
				log.info("Producto no encontrado con el id: {}", id);
				return new ResponseEntity<>(Map.of("mensaje", "Producto no encontrado"), HttpStatus.BAD_REQUEST);
			}
			
			Producto producto = productoOptional.get();
			producto.setNombreProducto(productoDTO.getNombreProducto());
			producto.setCodigo(productoDTO.getCodigo());
			producto.setMarca(productoDTO.getMarca());
			producto.setCantidad(productoDTO.getCantidad());
			producto.setPrecio_unitario(productoDTO.getPrecio_unitario());
			
			productoService.saveProducto(producto);
			
			log.info("Producto actualizado: {}", producto);
			return new ResponseEntity<>(producto, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error al actualizar el producto con el id: {}", id, e);
			return new ResponseEntity<>(Map.of("error", "Error al actualizar el producto con el id: " + id,
											   "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteProducto (@PathVariable ("id") String id){
		
		try {
			
			Optional<Producto> productoOptional = productoService.findProductoById(id);
			
			if (productoOptional.isEmpty()) {
				log.error("Error, no se encontró producto con el id: {}", id);
				return new ResponseEntity<>(Map.of("mensaje", "Error, no se encontró el producto con el id: " + id), HttpStatus.BAD_REQUEST);
			}
			
			productoService.deleteProducto(id);
			
			log.info("Producto eliminado con éxito !");
			return new ResponseEntity<>(Map.of("mensaje", "Producto eliminado con éxito"), HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error, al eliminar el producto con el id: {}", id);
			return new ResponseEntity<>(Map.of("error", "Error al eliminar el producto con el id: "+ id,
											   "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Método para generar el excel
	@GetMapping("/inventario-excel")
	public ResponseEntity<?> descargarInventarioExcel(){
		
		try {
			
			byte[] excelBytes = productoService.generarInventarioProductoExcel();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDispositionFormData("attachment", "productos-inventario.xlsx");
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			
			log.info("Excel generado OK");
			return new ResponseEntity<>(excelBytes, headers, HttpStatus.CREATED);
			
		} catch (Exception e) {
			log.error("Error al generar el excel", e);
			return new ResponseEntity<>(Map.of("detalle", "No se genero el excel del inventario", "error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Método para generar pdf
	@GetMapping("/inventario-pdf")
	public ResponseEntity<InputStreamResource> descargarInventarioPDF(){
		
		try {
			
			List<Producto> listProducto = productoService.findAllProductos();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			Document document = new Document(PageSize.A4, 50, 50, 50, 50);
			PdfWriter.getInstance(document, out);
			
			document.open();
			
			//Titulos y encabezados
			Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, Color.WHITE);
			Font infoFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
	        Font headerFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.WHITE);
	        Font montosFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.BLACK);
	        Font graciasFont = new Font(Font.HELVETICA, 14, Font.BOLD, Color.getHSBColor(0.54f, 0.71f, 0.86f));

	        // Encabezado Mejorado
	        PdfPTable tableEncabezado = new PdfPTable(2);
	        tableEncabezado.setWidthPercentage(100);
	        tableEncabezado.setSpacingAfter(10f);
	        tableEncabezado.setWidths(new float[]{50, 50});
	        
	     // Título centrado que ocupa las dos columnas
	        PdfPCell header0 = new PdfPCell(new Paragraph("INVENTARIO DE PRODUCTOS", titleFont));
	        header0.setColspan(2); // Ocupa las dos columnas
	        header0.setBackgroundColor(new Color(63, 169, 219)); // 0, 102, 204 Azul oscuro
	        header0.setHorizontalAlignment(Element.ALIGN_CENTER);
	        header0.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        header0.setPadding(15f);
	        tableEncabezado.addCell(header0);
	        document.add(tableEncabezado);
	        
	        document.add(new Paragraph("Luiz S.A.C", infoFont));
	        document.add(new Paragraph("RUC: 4250125244", infoFont));
	        document.add(new Paragraph("Mz L3 Lt35 Los Alamos", infoFont));
	        document.add(new Paragraph("CHACLACAYO - LIMA", infoFont));
	        document.add(new Paragraph("Fecha: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), infoFont));
	        document.add(Chunk.NEWLINE);
	        
	     // Tabla de Productos
	        PdfPTable table = new PdfPTable(4);
	        table.setWidthPercentage(100);
	        table.setWidths(new float[]{40, 20, 20, 20});

	        PdfPCell header3 = new PdfPCell(new Phrase("Código", headerFont));
	        header3.setBackgroundColor(new Color(63, 169, 219));
	        header3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header3);
	        
	        PdfPCell header4 = new PdfPCell(new Phrase("Nombre de Producto", headerFont));
	        header4.setBackgroundColor(new Color(63, 169, 219));
	        header4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header4);

	        PdfPCell header5 = new PdfPCell(new Phrase("Marca", headerFont));
	        header5.setBackgroundColor(new Color(63, 169, 219));
	        header5.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header5);
	        
	        PdfPCell header6 = new PdfPCell(new Phrase("Cantidad", headerFont));
	        header6.setBackgroundColor(new Color(63, 169, 219));
	        header6.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header6);

	        PdfPCell header7 = new PdfPCell(new Phrase("Precio Unitario", headerFont));
	        header7.setBackgroundColor(new Color(63, 169, 219));
	        header7.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header7);

	        
	        
	     // Agregar los productos a la tabla
	        for (Producto producto : listProducto) {
	        	PdfPCell cell1 = new PdfPCell(new Phrase(producto.getCodigo(), montosFont));
	            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell1);
	            
	            PdfPCell cell2 = new PdfPCell(new Phrase(producto.getNombreProducto(), montosFont));
	            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell2);

	            PdfPCell cell3 = new PdfPCell(new Phrase(producto.getMarca(), montosFont));
	            cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell3);
	            
	            PdfPCell cell4 = new PdfPCell(new Phrase(String.valueOf(producto.getCantidad()), montosFont));
	            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell4);

	            PdfPCell cell5 = new PdfPCell(new Phrase(String.format("%.2f", producto.getPrecio_unitario()), montosFont));
	            cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
	            table.addCell(cell5);

	            
	        }
	        
	        document.add(table);
	        document.add(Chunk.NEWLINE);
	        
	        document.close();

	        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=producto-inventario.pdf");

	        log.info("Reporte de Inventario de Productos PDF OK! ");
	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentType(MediaType.APPLICATION_PDF)
	                .body(new InputStreamResource(bis));
			
		} catch (Exception e) {
			log.error("Error al crear el PDF del inventario de los producto: {}", e.getMessage(), e) ;
			return ResponseEntity.internalServerError().build();
		}
	}
	
	//Método para importar excel
	@PostMapping("/importar-excel")
	public ResponseEntity<?> importExcelProductos(@RequestParam ("file") MultipartFile file){
		
		try {
			
			
			if (file.isEmpty()) {
				return new ResponseEntity<>(Map.of("mensaje", "Error, el archivo esta vacio"), HttpStatus.BAD_REQUEST);
			}
			
			List<Producto> productosImportados = productoService.importarExcelProductos(file);
			
			if (productosImportados.isEmpty()) {
				return new ResponseEntity<>(Map.of("mensaje", "No se importaron productos (posiblemente ya existian)"), HttpStatus.NOT_FOUND);
			}
			
			log.info("Excel importado exitosamente");
			return new ResponseEntity<>(productosImportados, HttpStatus.OK);
			
		} catch (Exception e) {
			log.error("Error al importar el excel de productos: {}", e);
			return new ResponseEntity<>(Map.of("error", "Error al importar el excel de productos",
												"detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
