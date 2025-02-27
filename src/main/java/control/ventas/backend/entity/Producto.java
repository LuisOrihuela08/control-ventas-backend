package control.ventas.backend.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

	private String nombre_producto;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
}
