package pedido;

public class GestorTiempoPedido {
    private int tiempoRestante;

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
}

