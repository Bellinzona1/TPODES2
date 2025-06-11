package pedido;

import modelo.MetodoPago;

public class FabricaPago {
    public static Pago crearPago(MetodoPago metodo, double monto) {
        switch (metodo) {
            case EFECTIVO: return new PagoEfectivo(monto);
            case MERCADOPAGO: return new PagoMP(monto);
            case GOOGLE_PAY: return new PagoGoogle(monto);
            default: throw new IllegalArgumentException("Método de pago inválido");
        }
    }
}