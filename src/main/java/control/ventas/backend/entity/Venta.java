package control.ventas.backend.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "venta")
public class Venta {

	@Id
	private String id;
	private List<Producto> productos_vendidos;
	private double monto_total;
	private String metodo_pago;
	private double dinero_cliente;
	private double vuelto;
	@Field("fecha_compra")
	private LocalDateTime fechaCompra;
	
}


