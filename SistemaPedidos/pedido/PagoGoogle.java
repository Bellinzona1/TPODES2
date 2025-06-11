
package pedido;

public class PagoGoogle extends Pago {
    public PagoGoogle(double monto) { super(monto); }
    public double procesar() { return monto; }
}