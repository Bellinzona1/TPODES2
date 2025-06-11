package pedido;

public class PagoEfectivo extends Pago {
    public PagoEfectivo(double monto) { super(monto); }
    public double procesar() { return monto * 0.9; }
}