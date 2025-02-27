package control.ventas.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDTO {

	private String nombre_producto;
    private int cantidad;
    private double precio_unitario;
}
