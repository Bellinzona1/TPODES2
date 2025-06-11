package modelo;

public class Producto {
    private String nombre;
    private double precio;
    private int tiempoPreparacion;

    public Producto(String nombre, double precio, int tiempoPreparacion) {
        this.nombre = nombre;
        this.precio = precio;
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public String getNombre() {
        return nombre;
    }
}