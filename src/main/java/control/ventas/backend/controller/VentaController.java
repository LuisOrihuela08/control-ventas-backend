package control.ventas.backend.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import java.awt.Color;

import control.ventas.backend.dto.ProductoDTO;
import control.ventas.backend.dto.ReporteVentasPorMetodoPagoDTO;
import control.ventas.backend.dto.VentaDTO;
import control.ventas.backend.entity.Producto;
import control.ventas.backend.entity.Venta;
import control.ventas.backend.service.VentaService;

@RestController
@RequestMapping("/api-venta")
public class VentaController {

	@Autowired
	private VentaService ventaService;

	private final Logger logger = LoggerFactory.getLogger(VentaController.class);

	@GetMapping("/list-all")
	public ResponseEntity<?> getAllVentas() {

		try {

			List<Venta> listVentas = ventaService.findAllVentas();
			logger.info("Listado de las ventas OK");
			return new ResponseEntity<>(listVentas, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error al listar las Ventas", e);
			return new ResponseEntity<>(Map.of("error", "Error al listar las ventas", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Listar ventas con paginacion
	@GetMapping("/list-venta-page")
	public ResponseEntity<?> getVentasByPage(@RequestParam("page") int page, @RequestParam ("size") int size){
		
		try {
			
			Page<Venta> listVentasByPage = ventaService.listVentaByPages(page, size);
			logger.info("Ventas paginadas OK");
			return new ResponseEntity<>(listVentasByPage, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("ERROR al listar ventas paginadas", e);
			return new ResponseEntity<>(Map.of("error", "Hubo un error al listar ventas paginadas", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<?> registerVenta(@RequestBody VentaDTO ventaDTO) {

		try {
			
			Venta ventaNueva = new Venta();
	        ventaNueva.setMetodo_pago(ventaDTO.getMetodo_pago());
	        ventaNueva.setDinero_cliente(ventaDTO.getDinero_cliente());
	        ventaNueva.setFechaCompra(ventaDTO.getFecha_compra());

	        // Cálculos de subtotal y total
	        double montoTotal = 0;
	        List<Producto> productosVendidos = new ArrayList<>();
	        
	        for (ProductoDTO productoDTO : ventaDTO.getProductos_vendidos()) {
	            Producto producto = new Producto();
	            producto.setNombre_producto(productoDTO.getNombre_producto());
	            producto.setCantidad(productoDTO.getCantidad());
	            producto.setPrecio_unitario(productoDTO.getPrecio_unitario());
	            producto.setMarca(productoDTO.getMarca());

	            // Calculamos subtotal por producto
	            double subtotal = productoDTO.getCantidad() * productoDTO.getPrecio_unitario();
	            producto.setSubtotal(subtotal);
	            montoTotal += subtotal;
	            productosVendidos.add(producto);
	        }
	       
	        ventaNueva.setProductos_vendidos(productosVendidos);
	        ventaNueva.setMonto_total(montoTotal);
	        ventaNueva.setVuelto(ventaNueva.getDinero_cliente() - montoTotal);
	        

	        ventaService.registerVenta(ventaNueva);
	        logger.info("Venta creada {}", ventaNueva);
	        return new ResponseEntity<>(ventaNueva, HttpStatus.CREATED);

		} catch (Exception e) {
			logger.error("Error al crear la venta", e);
			return new ResponseEntity<>(Map.of("error", "Error al crear la venta", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	/*
	//Crear un PDF en formato boleta
	 @GetMapping("/export/pdf/{id}")
	    public ResponseEntity<InputStreamResource> exportVentaToPDF(@PathVariable("id") String id) {
	        try {
	            Venta venta = ventaService.findVentaById(id);

	            if (venta == null) {
	                return ResponseEntity.notFound().build();
	            }

	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            Document document = new Document();
	            PdfWriter.getInstance(document, out);

	            document.open();
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("                BOLETA DE COMPRA                  "));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("                  Tienda Luiz S.A.C                  "));
	            document.add(new Paragraph("                    4250125244                      "));
	            document.add(new Paragraph("      Mz L3 Lt35 CHACLACAYO - LIMA           "));
	            document.add(new Paragraph("               Tienda Luiz S.A.C                  "));
	            document.add(new Paragraph("        Fecha: " + venta.getFecha_compra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
	      
	            document.add(new Paragraph("---------------------------------------------------------"));
	            document.add(new Paragraph("Productos:"));
	            venta.getProductos_vendidos().forEach(producto -> {
	                try {
	                    document.add(new Paragraph(producto.getNombre_producto() + " - Cant: " + producto.getCantidad() + 
	                            " x S/." + producto.getPrecio_unitario() + " = S/." + (producto.getCantidad() * producto.getPrecio_unitario())));
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }
	            });

	            document.add(new Paragraph("---------------------------------------------------------"));
	            document.add(new Paragraph("Monto Total: S/." + venta.getMonto_total()));
	            document.add(new Paragraph("Dinero Cliente: S/." + venta.getDinero_cliente()));
	            document.add(new Paragraph("Método de pago: " + venta.getMetodo_pago()));
	            document.add(new Paragraph("Vuelto: S/." + venta.getVuelto()));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("                Gracias por su compra             "));
	            document.add(new Paragraph("**************************************************"));
	            document.add(new Paragraph("**************************************************"));
	            
	            document.close();

	            ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
	            HttpHeaders headers = new HttpHeaders();
	            headers.add("Content-Disposition", "attachment; filename=boleta-compra.pdf");

	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentType(MediaType.APPLICATION_PDF)
	                    .body(new InputStreamResource(bis));
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.internalServerError().build();
	        }
	    }
	*/
	@GetMapping("/export/pdf/{id}")
	public ResponseEntity<InputStreamResource> exportVentaToPDF(@PathVariable("id") String id) {
	    try {
	        Venta venta = ventaService.findVentaById(id);

	        if (venta == null) {
	            return ResponseEntity.notFound().build();
	        }

	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        Document document = new Document(PageSize.A4, 50, 50, 50, 50);
	        PdfWriter.getInstance(document, out);

	        document.open();

	        // Títulos y Encabezados
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
	        PdfPCell header0 = new PdfPCell(new Paragraph("NOTA DE VENTA", titleFont));
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
	        document.add(new Paragraph("Fecha: " + venta.getFechaCompra().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), infoFont));
	        document.add(Chunk.NEWLINE);

	        // Tabla de Productos
	        PdfPTable table = new PdfPTable(4);
	        table.setWidthPercentage(100);
	        table.setWidths(new float[]{40, 20, 20, 20});

	        PdfPCell header3 = new PdfPCell(new Phrase("Producto", headerFont));
	        header3.setBackgroundColor(new Color(63, 169, 219));
	        header3.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header3);

	        PdfPCell header4 = new PdfPCell(new Phrase("Cantidad", headerFont));
	        header4.setBackgroundColor(new Color(63, 169, 219));
	        header4.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header4);

	        PdfPCell header5 = new PdfPCell(new Phrase("Precio Unitario", headerFont));
	        header5.setBackgroundColor(new Color(63, 169, 219));
	        header5.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header5);

	        PdfPCell header6 = new PdfPCell(new Phrase("Subtotal", headerFont));
	        header6.setBackgroundColor(new Color(63, 169, 219));
	        header6.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(header6);

	        venta.getProductos_vendidos().forEach(producto -> {
	            table.addCell(new PdfPCell(new Phrase(producto.getNombre_producto(), infoFont)));
	            table.addCell(new PdfPCell(new Phrase(String.valueOf(producto.getCantidad()), infoFont)));
	            table.addCell(new PdfPCell(new Phrase("S/." + producto.getPrecio_unitario(), infoFont)));
	            table.addCell(new PdfPCell(new Phrase("S/." + (producto.getCantidad() * producto.getPrecio_unitario()), infoFont)));
	        });

	        document.add(table);
	        document.add(Chunk.NEWLINE);

	        // Totales
	        Paragraph total = new Paragraph("Monto Total: S/." + venta.getMonto_total(), montosFont);
	        total.setAlignment(Element.ALIGN_RIGHT);
	        document.add(total);

	        Paragraph dineroCliente = new Paragraph("Dinero Cliente: S/." + venta.getDinero_cliente(), montosFont);
	        dineroCliente.setAlignment(Element.ALIGN_RIGHT);
	        document.add(dineroCliente);
	        
	        Paragraph metodoPago = new Paragraph("Método de pago: " + venta.getMetodo_pago(), montosFont);
	        metodoPago.setAlignment(Element.ALIGN_RIGHT);
	        document.add(metodoPago);
	        
	        Paragraph vuelto = new Paragraph("Vuelto: S/." + venta.getVuelto(), montosFont);
	        vuelto.setAlignment(Element.ALIGN_RIGHT);
	        document.add(vuelto);

	        document.add(Chunk.NEWLINE);

	        Paragraph gracias = new Paragraph("Gracias por su compra", graciasFont);
	        gracias.setAlignment(Element.ALIGN_CENTER);
	        document.add(gracias);

	        document.close();

	        ByteArrayInputStream bis = new ByteArrayInputStream(out.toByteArray());
	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Disposition", "attachment; filename=boleta-compra.pdf");

	        return ResponseEntity.ok()
	                .headers(headers)
	                .contentType(MediaType.APPLICATION_PDF)
	                .body(new InputStreamResource(bis));
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.internalServerError().build();
	    }
	}

	

	@GetMapping("/find-venta/{id}")
	public ResponseEntity<?> getVentaById(@PathVariable("id") String id) {

		try {

			Venta ventaEncontrada = ventaService.findVentaById(id);
			
			if (ventaEncontrada == null) {
				logger.error("Venta no encontrada en la base de datos");
				return new ResponseEntity<>("Error al encontrar la venta", HttpStatus.BAD_REQUEST);
			}
			
			logger.info("Venta encontrada {}", ventaEncontrada);
			return new ResponseEntity<>(ventaEncontrada, HttpStatus.OK);

		} catch (Exception e) {
			logger.error("Error al encontrar la venta {}", e);
			return new ResponseEntity<>(Map.of("error", "Error al encontrar la venta", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Método para buscar ventas entre fechas
	@GetMapping("/buscar-por-fecha")
	public ResponseEntity<?> getVentasBetweenFecha(@RequestParam("fechaInicio") String  fechaInicio, @RequestParam ("fechaFin") String  fechaFin){
		
		try {
			
			if (fechaInicio == null || fechaFin == null) {
				return new ResponseEntity<>(Map.of("error", "Por favor ingrese ambas fechas"), HttpStatus.BAD_REQUEST);
			}
			
			List<Venta> ventas = ventaService.findVentasBetweenFecha(fechaInicio, fechaFin);
			
			if (ventas.isEmpty()) {
	            return new ResponseEntity<>(Map.of("mensaje", "No se encontraron ventas en el rango de fechas"), HttpStatus.NOT_FOUND);
	        }
			
			logger.info("Busqueda exitosa");
			return ResponseEntity.ok(ventas);
		
		} catch (Exception e) {
			logger.error("Error en el filtro buscar-por-fecha", e);
			return new ResponseEntity<>(Map.of("error", "Error al filtrar las ventas por las fechas", "detalles", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Método para buscar venta por nombre del producto
	@GetMapping("/buscar-venta/{nombreProducto}")
	public ResponseEntity<?> getVentaByNombreProducto(@PathVariable("nombreProducto") String nombreProducto){
		
		try {
			
			List<Venta> ventaEncontrada = ventaService.findVentaByNombreProducto(nombreProducto);
			
			logger.info("Venta Encontrada OK");
			return new ResponseEntity<>(ventaEncontrada, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("ERROR AL ENCONTRAR LA VENTA", e);
			return new ResponseEntity<>(Map.of("error", "Error al encontrar la venta por el nombre del producto", "detalle", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//Método para reportar las ventas por el metodo de pago
	@GetMapping("/buscar-venta/metodo-pago")
	public ResponseEntity<?> getVentaReporteByMetodoPago(){
		
		try {
			
			List<ReporteVentasPorMetodoPagoDTO> reporte = ventaService.findVentasByMetodoPago();
			
			logger.info("Reporte de Ventas por Metodo de pago OK");
			return new ResponseEntity<>(reporte, HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error("ERROR al obtener el reporte: getVentaReporteByMetodoPago ", e);
			return new ResponseEntity<>(Map.of("detalle", "Error al obtener el reporte de venta por metodo de pago", "error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
