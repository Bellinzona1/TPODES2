package pedido;

public class PagoMP extends Pago {
    public PagoMP(double monto) { super(monto); }
    public double procesar() { return monto; }
}