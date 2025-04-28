package control.ventas.backend.entity;
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
@Document(collection = "producto")
public class Producto {

	@Id
	private String id;
	@Field(name = "nombre_producto")
	private String nombreProducto;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
	private String marca;
	
	
	//Este constructor es para el Test Unitario
	public Producto(String nombreProducto, int cantidad, double precio_unitario, String marca) {
		super();
		this.nombreProducto = nombreProducto;
		this.cantidad = cantidad;
		this.precio_unitario = precio_unitario;
		this.marca = marca;
	}
}
