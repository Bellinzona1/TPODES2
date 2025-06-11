package pedido;

import java.util.List;
import modelo.Producto;

public class CalculadoraTotalPedido {
    public static double calcularTotal(List<Producto> productos) {
        return productos.stream().mapToDouble(Producto::getPrecio).sum();
    }
}

