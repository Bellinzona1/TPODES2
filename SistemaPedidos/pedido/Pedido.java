package pedido;

import modelo.*;
import estrategia.*;
import java.util.*;
import pedido.CalculadoraTotalPedido;
import pedido.GestorTiempoPedido;
import pedido.ProgramacionPedido;

public class Pedido {
    private List<Producto> productos = new ArrayList<>();
    private EstadoPedido estado = EstadoPedido.EN_ESPERA;
    private Cliente cliente;
    private MetodoPago metodoPago;
    private Date programadoPara;
    private boolean esDelivery;
    private GestorTiempoPedido gestorTiempo;
    private ProgramacionPedido programacion = new ProgramacionPedido();

    public Pedido(Cliente cliente, MetodoPago metodoPago, boolean esDelivery) {
        this.cliente = cliente;
        this.metodoPago = metodoPago;
        this.esDelivery = esDelivery;
        this.gestorTiempo = new GestorTiempoPedido(0);
    }

    public void agregarProducto(Producto producto) {
        if (estado == EstadoPedido.EN_ESPERA) {
            productos.add(producto);
        } else {
            throw new IllegalStateException("Solo se puede agregar productos si el pedido está en espera");
        }
    }

    public double cancelar() {
        if (estado == EstadoPedido.EN_ESPERA || estado == EstadoPedido.EN_PREPARACION) {
            estado = EstadoPedido.CANCELADO;
            return CalculadoraTotalPedido.calcularTotal(productos) * 0.75;
        } else {
            throw new IllegalStateException("No se puede cancelar en este estado");
        }
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    // Métodos para acceder a las clases auxiliares
    public CalculadoraTotalPedido getCalculadoraTotal() {
        return new CalculadoraTotalPedido();
    }

    public GestorTiempoPedido getGestorTiempo() {
        return gestorTiempo;
    }

    public ProgramacionPedido getProgramacion() {
        return programacion;
    }
}
