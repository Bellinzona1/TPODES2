package estrategia;

import pedido.Pedido;

public class TiempoDelivery implements EstrategiaTiempo {
    private EstrategiaTiempo estrategiaBase;
    private int tiempoRappi;

    public TiempoDelivery(EstrategiaTiempo estrategiaBase, Integer tiempoRappi) {
        this.estrategiaBase = estrategiaBase;
        this.tiempoRappi = (tiempoRappi != null) ? tiempoRappi : 0;
    }

    @Override
    public int calcular(Pedido pedido, int pedidosEnEspera) {
        return estrategiaBase.calcular(pedido, pedidosEnEspera) + tiempoRappi;
    }
}
