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
	private String codigo;
	private int cantidad;
	private double precio_unitario;
	private double subtotal;
	private String marca;
	private String descripcion;
	
	
	//Este constructor es para el Test Unitario
	/*
	public Producto(String nombreProducto, String codigo, int cantidad, double precio_unitario, String marca) {
		super();
		this.nombreProducto = nombreProducto;
		this.codigo = codigo;
		this.cantidad = cantidad;
		this.precio_unitario = precio_unitario;
		this.marca = marca;
	}
*/

	public Producto(String id, String nombreProducto, String codigo, int cantidad, double precio_unitario, String marca,
			String descripcion) {
		super();
		this.id = id;
		this.nombreProducto = nombreProducto;
		this.codigo = codigo;
		this.cantidad = cantidad;
		this.precio_unitario = precio_unitario;
		this.marca = marca;
		this.descripcion = descripcion;
	}
	
	
}
