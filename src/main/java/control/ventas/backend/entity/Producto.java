package control.ventas.backend.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "producto")
public class Producto {

	@Id
	private String id;
	private String nombre_producto;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
	private String marca;
	
	
	//Este constructor es para el Test Unitario
	public Producto(String nombre_producto, int cantidad, double precio_unitario, String marca) {
		super();
		this.nombre_producto = nombre_producto;
		this.cantidad = cantidad;
		this.precio_unitario = precio_unitario;
		this.marca = marca;
	}
}
