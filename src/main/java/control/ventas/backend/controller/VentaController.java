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

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import control.ventas.backend.dto.ProductoDTO;
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

	@GetMapping("/list")
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

	@PostMapping("/register")
	public ResponseEntity<?> registerVenta(@RequestBody VentaDTO ventaDTO) {

		try {
			
			Venta ventaNueva = new Venta();
	        ventaNueva.setMetodo_pago(ventaDTO.getMetodo_pago());
	        ventaNueva.setDinero_cliente(ventaDTO.getDinero_cliente());
	        ventaNueva.setFecha_compra(ventaDTO.getFecha_compra());

	        // Cálculos de subtotal y total
	        double montoTotal = 0;
	        List<Producto> productosVendidos = new ArrayList<>();
	        
	        for (ProductoDTO productoDTO : ventaDTO.getProductos_vendidos()) {
	            Producto producto = new Producto();
	            producto.setNombre_producto(productoDTO.getNombre_producto());
	            producto.setCantidad(productoDTO.getCantidad());
	            producto.setPrecio_unitario(productoDTO.getPrecio_unitario());

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
}
