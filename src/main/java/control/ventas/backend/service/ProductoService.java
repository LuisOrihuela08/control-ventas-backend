package control.ventas.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import control.ventas.backend.entity.Producto;
import control.ventas.backend.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	//Método para buscar producto por nombre
	public Page<Producto> findProductoByNombre(String nombreProducto, int page, int size){
		
		try {
			
			Pageable pageable = PageRequest.of(page, size);
			return productoRepository.findProductoByNombreProducto(nombreProducto, pageable);
			
		} catch (Exception e) {
			log.error("Error al buscar producto por nombre: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por nombre: " + e.getMessage());
		}
		
		
	}
	
	//Método para buscar producto por marca
	public Page<Producto> findProductoByMarca (String marca, int page, int size){
		
		try {
			
			Pageable pageable = PageRequest.of(page, size);
			return productoRepository.findProductoByMarca(marca, pageable);
			
		} catch (Exception e) {
			log.error("Error al buscar producto por marca: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar el producto por la marca: " + e.getMessage());
		}
		
	}
	
	public void deleteProducto(String id) {
		productoRepository.deleteById(id);
	}
	
	//Método para generar un excel con los productos registrados
	public byte[] generarInventarioProductoExcel() throws IOException {
		List<Producto> productos = productoRepository.findAll();
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Inventario de Productos");
		
		Row headerRow = sheet.createRow(0);
		String[] columnas = {"ID", "Nombre", "Marca", "Precio Unitario", "Cantidad"};
		
		for (int i = 0; i < columnas.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columnas[i]);
			cell.setCellStyle(crearEstiloEncabezado(workbook));
		}
		
		//Llenamos el excel de inventario con los productos
		int rowNum = 1;
		for(Producto producto: productos) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(producto.getId());
			row.createCell(1).setCellValue(producto.getNombreProducto());
			row.createCell(2).setCellValue(producto.getMarca());
			row.createCell(3).setCellValue(producto.getPrecio_unitario());
			//row.createCell(4).setCellValue(producto.getSubtotal());
			row.createCell(4).setCellValue(producto.getCantidad());
		}
		
		//Esto es para ajustar el tamaño de las columnas
		for (int i = 0; i < columnas.length; i++) {
			sheet.autoSizeColumn(i);
		}
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();
		
		return outputStream.toByteArray();
	}
	
	private CellStyle crearEstiloEncabezado(Workbook workbook) {
		CellStyle estilo = workbook.createCellStyle();
		Font font = workbook.createFont();
		font.setBold(true);
		estilo.setFont(font);
		return estilo;
	}
}
