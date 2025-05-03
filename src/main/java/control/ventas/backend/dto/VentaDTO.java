package control.ventas.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VentaDTO {
    private List<ProductoDTO> productos_vendidos;
    private double monto_total;
    private String metodoPago;
    private double dinero_cliente;
    private LocalDateTime fecha_compra;
}


