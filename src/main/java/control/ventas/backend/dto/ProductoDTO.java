package control.ventas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {

	private String nombreProducto;
	private String codigo;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
	private String marca;
}
