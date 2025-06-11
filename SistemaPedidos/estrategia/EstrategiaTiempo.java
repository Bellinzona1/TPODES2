package estrategia;

import pedido.Pedido;

public interface EstrategiaTiempo {
    int calcular(Pedido pedido, int pedidosEnEspera);
}