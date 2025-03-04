package control.ventas.backend.dto;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteVentasPorMetodoPagoDTO {

	@Field("_id")
	private String metodoPago;
    private Double totalVentas;
    private Integer cantidadVentas;

}
