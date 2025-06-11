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

    public double calcularTotal() {
        return CalculadoraTotalPedido.calcularTotal(productos);
    }

    public double cancelar() {
        if (estado == EstadoPedido.EN_ESPERA || estado == EstadoPedido.EN_PREPARACION) {
            estado = EstadoPedido.CANCELADO;
            return calcularTotal() * 0.75;
        } else {
            throw new IllegalStateException("No se puede cancelar en este estado");
        }
    }

    public int calcularTiempoEstimado(int pedidosEnEspera, Integer tiempoRappi) {
        EstrategiaTiempo estrategia;
        if (estado == EstadoPedido.EN_ESPERA) {
            estrategia = new TiempoEnEspera();
        } else if (estado == EstadoPedido.EN_PREPARACION) {
            estrategia = new TiempoEnPreparacion();
        } else {
            return 0;
        }
        if (esDelivery) {
            estrategia = new estrategia.TiempoDelivery(estrategia, tiempoRappi);
        }
        return estrategia.calcular(this, pedidosEnEspera);
    }

    public double procesarPago() {
        Pago pago = FabricaPago.crearPago(metodoPago, calcularTotal());
        return pago.procesar();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void programarPara(Date fechaHora) {
        programacion.programarPara(fechaHora);
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido nuevoEstado) {
        this.estado = nuevoEstado;
        if (nuevoEstado == EstadoPedido.EN_PREPARACION) {
            int tiempo = this.productos.stream().mapToInt(Producto::getTiempoPreparacion).sum();
            if (esDelivery) tiempo += main.App.tiempoDelivery;
            this.gestorTiempo.setTiempoRestante(tiempo);
        }
    }

    public int getTiempoRestante() {
        return gestorTiempo.getTiempoRestante();
    }

    public void restarMinuto() {
        gestorTiempo.restarMinuto();
    }

    public boolean esDelivery() {
        return esDelivery;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
}
