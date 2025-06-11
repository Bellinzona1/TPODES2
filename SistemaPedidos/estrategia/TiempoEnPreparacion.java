package estrategia;

import pedido.Pedido;

public class TiempoEnPreparacion implements EstrategiaTiempo {
    public int calcular(Pedido pedido, int pedidosEnEspera) {
        return pedido.getProductos().stream().mapToInt(p -> p.getTiempoPreparacion()).sum();
    }
}