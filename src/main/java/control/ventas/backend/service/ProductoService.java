package control.ventas.backend.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import control.ventas.backend.entity.Producto;
import control.ventas.backend.repository.ProductoRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductoService {

	@Autowired
	private ProductoRepository productoRepository;

	public List<Producto> findAllProductos() {
		return productoRepository.findAll();
	}

	// Listar con productos con paginacion
	public Page<Producto> findProductosByPagination(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		return productoRepository.findAll(pageable);
	}

	public Producto saveProducto(Producto producto) {
		return productoRepository.save(producto);
	}

	public Optional<Producto> findProductoById(String id) {
		return productoRepository.findById(id);
	}

	// Método para buscar producto por nombre
	public Page<Producto> findProductoByNombre(String nombreProducto, int page, int size) {

		try {

			Pageable pageable = PageRequest.of(page, size);
			return productoRepository.findProductoByNombreProducto(nombreProducto, pageable);

		} catch (Exception e) {
			log.error("Error al buscar producto por nombre: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por nombre: " + e.getMessage());
		}

	}

	// Este método lo implemente para buscar productos por el nombre y poder
	// registrar una venta con productos
	// que existen
	public List<Producto> findByNombreProductoForVenta(String nombreProducto) {

		try {
			return productoRepository.findVentaByNombreProducto(nombreProducto);
		} catch (Exception e) {
			log.error("Error al buscar producto por nombre: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por nombre: " + e.getMessage());
		}
	}
	
	//Método para buscar producto por codigo
	public Producto findProductoByCodigo(String codigo) {
		
		try {
			return productoRepository.findByCodigo(codigo);
		} catch (Exception e) {
			log.error("Error al buscar producto por su código: {}", e, e.getMessage());
			throw new RuntimeException("Error, al buscar por su código: " + e.getMessage());
		}
	}

	// Método para buscar producto por marca
	public Page<Producto> findProductoByMarca(String marca, int page, int size) {

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

	// Método para generar un excel con los productos registrados
	public byte[] generarInventarioProductoExcel() throws IOException {
		List<Producto> productos = productoRepository.findAll();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Inventario de Productos");

		Row headerRow = sheet.createRow(0);
		String[] columnas = { "ID", "Código", "Nombre Producto", "Marca", "Precio Unitario", "Cantidad" };

		for (int i = 0; i < columnas.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columnas[i]);
			cell.setCellStyle(crearEstiloEncabezado(workbook));
		}

		// Llenamos el excel de inventario con los productos
		int rowNum = 1;
		for (Producto producto : productos) {
			Row row = sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(producto.getId());
			row.createCell(1).setCellValue(producto.getCodigo());
			row.createCell(2).setCellValue(producto.getNombreProducto());
			row.createCell(3).setCellValue(producto.getMarca());
			row.createCell(4).setCellValue(producto.getPrecio_unitario());
			// row.createCell(4).setCellValue(producto.getSubtotal());
			row.createCell(5).setCellValue(producto.getCantidad());
		}

		// Esto es para ajustar el tamaño de las columnas
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

	// Método para importar excel
	public List<Producto> importarExcelProductos(MultipartFile file) throws Exception {

		List<Producto> productos = new ArrayList<>();

		try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {

			Sheet sheet = workbook.getSheetAt(0);

			// Empieza desde la fila 8 (índice 7)
			for (int i = 7; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String codigo = getCellValue(row.getCell(1)); // Columna B
				String nombreProducto = getCellValue(row.getCell(2)); // Columna B
				String marca = getCellValue(row.getCell(6)); // Columna G
				String precioUnitarioStr = getCellValue(row.getCell(9)); // Columna J
				String cantidadStr = getCellValue(row.getCell(11)); // Columna L

				if (nombreProducto == null || nombreProducto.isEmpty())
					continue;

				double precio = Double.parseDouble(precioUnitarioStr);
				int cantidad = (int) Double.parseDouble(cantidadStr);

				/*
				 * Esto es solo para para verificar si existe el producto por el nombre //
				 * Verificar si ya existe por nombre if
				 * (!productoRepository.existsByNombreProducto(nombreProducto)) { Producto
				 * producto = new Producto(); producto.setNombreProducto(nombreProducto);
				 * producto.setMarca(marca); producto.setPrecio_unitario(precio);
				 * producto.setCantidad(cantidad);
				 * 
				 * productos.add(producto); }
				 */

				// Esto no solo verifica si existe por nombre el producto
				// Sino que actualiza los registros como cantidad, marca, precio
				Optional<Producto> productoExistenteOpt = productoRepository.getByNombreProducto(nombreProducto);

				if (productoExistenteOpt.isPresent()) {
					Producto productoExistente = productoExistenteOpt.get();
					productoExistente.setCodigo(codigo);
					productoExistente.setCantidad(productoExistente.getCantidad() + cantidad);
					productoExistente.setPrecio_unitario(precio); // Puedes comentar esta línea si no quieres actualizar
																	// el precio
					productoExistente.setMarca(marca); // Igual con la marca
					productos.add(productoExistente);
				} else {
					Producto nuevoProducto = new Producto();
					nuevoProducto.setCodigo(codigo);
					nuevoProducto.setNombreProducto(nombreProducto);
					nuevoProducto.setMarca(marca);
					nuevoProducto.setPrecio_unitario(precio);
					nuevoProducto.setCantidad(cantidad);
					productos.add(nuevoProducto);
				}

			}
			return productoRepository.saveAll(productos);

		} catch (Exception e) {
			e.printStackTrace(); // o usa un logger si quieres
			throw e; // relanza para que el Controller lo capture
		}
	}

	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue();
		case NUMERIC:
			return String.valueOf(cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return "";
		}
	}

}
