package estrategia;

import pedido.Pedido;

public class TiempoEnEspera implements EstrategiaTiempo {
    public int calcular(Pedido pedido, int pedidosEnEspera) {
        if (pedidosEnEspera < 10) return 5;
        return 20 + 5 * (pedidosEnEspera / 10);
    }
}