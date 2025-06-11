package pedido;

public class GestorTiempoPedido {
    private int tiempoRestante;


    public boolean esDelivery;

    public GestorTiempoPedido(boolean esDelivery) {
        this.esDelivery = esDelivery;
        this.tiempoRestante = 0; // Tiempo inicial por defecto
    }

    public GestorTiempoPedido(int tiempoInicial) {
        this.tiempoRestante = tiempoInicial;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void restarMinuto() {
        if (tiempoRestante > 0) tiempoRestante--;
    }

    public void setTiempoRestante(int tiempo) {
        this.tiempoRestante = tiempo;
    }

    public int calcularTiempoEstimado(int cantidadPedidos, int tiempoDelivery) {
        // Ejemplo simple: 10 min por pedido + tiempo de delivery si corresponde
        int tiempoBase = 10 * cantidadPedidos;
        if (esDelivery) {
            tiempoBase += tiempoDelivery;
        }
        return tiempoBase;
    }
}

