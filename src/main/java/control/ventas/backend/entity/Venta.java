package control.ventas.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "venta")
public class Venta {

	@Id
	private String id;
	private List<ItemVenta> productos_vendidos;
	private double monto_total;
	private double dinero_cliente;
	private double vuelto;
	private LocalDateTime fecha_compra;
	
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class ItemVenta{
	
	private String nombre_producto;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
}

