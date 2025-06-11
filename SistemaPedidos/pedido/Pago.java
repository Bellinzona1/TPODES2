package pedido;

public abstract class Pago {
    protected double monto;

    public Pago(double monto) {
        this.monto = monto;
    }

    public abstract double procesar();
}