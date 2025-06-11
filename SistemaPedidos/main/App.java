package main;

import java.util.*;
import java.util.concurrent.*;
import modelo.*;
import pedido.*;

public class App {
    public static List<Producto> catalogo = new ArrayList<>();
    public static List<Pedido> pedidosActivos = new CopyOnWriteArrayList<>();
    public static int tiempoDelivery = 0;
    public static Set<String> cuponesValidos = new HashSet<>(Arrays.asList("DESC10", "PRIMERA"));

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                procesarPedidos();
            }
        }, 0, 60 * 1000); // cada minuto

        Scanner scanner = new Scanner(System.in);
        boolean activo = true;

        while (activo) {
            System.out.println("\nSeleccione modo de operación:");
            System.out.println("1 - Modo Cliente");
            System.out.println("2 - Modo Restaurante");
            System.out.println("0 - Salir");
            int modo = scanner.nextInt();
            scanner.nextLine();

            switch (modo) {
                case 1:
                    modoCliente(scanner);
                    break;
                case 2:
                    modoRestaurante(scanner);
                    break;
                case 0:
                    activo = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }

        scanner.close();
        timer.cancel();
    }

    public static void procesarPedidos() {
        for (Pedido pedido : pedidosActivos) {
            if (pedido.getEstado() == EstadoPedido.EN_ESPERA) {
                pedido.setEstado(EstadoPedido.EN_PREPARACION);
                System.out.println("[INFO] Pedido de " + pedido.getCliente().getNombre() + " pasó a EN_PREPARACION");
            } else if (pedido.getEstado() == EstadoPedido.EN_PREPARACION) {
                pedido.restarMinuto();
                if (pedido.getTiempoRestante() <= 0) {
                    pedidosActivos.remove(pedido);
                    System.out.println("[ENTREGADO] Pedido de " + pedido.getCliente().getNombre() + " ha sido entregado.");
                }
            }
        }
    }

    public static void modoCliente(Scanner scanner) {
        System.out.print("¿Está usando un Tótem o la App? (1 - Tótem, 2 - App): ");
        int origen = scanner.nextInt();
        scanner.nextLine();
        boolean esTotem = (origen == 1);

        System.out.print("Ingrese nombre del cliente: ");
        String nombre = scanner.nextLine();
        Cliente cliente = new Cliente(nombre);

        System.out.println("Seleccione método de pago:");
        System.out.println("1 - MercadoPago\n2 - Google Pay\n3 - Efectivo");
        int metodo = scanner.nextInt();
        scanner.nextLine();

        MetodoPago metodoPago;
        switch (metodo) {
            case 1: metodoPago = MetodoPago.MERCADOPAGO; break;
            case 2: metodoPago = MetodoPago.GOOGLE_PAY; break;
            case 3: metodoPago = MetodoPago.EFECTIVO; break;
            default: throw new IllegalArgumentException("Método inválido");
        }

        System.out.println("¿Es un pedido con delivery? (s/n)");
        boolean esDelivery = scanner.nextLine().trim().equalsIgnoreCase("s");

        Pedido pedido = new Pedido(cliente, metodoPago, esDelivery);

        boolean continuar = true;
        boolean pedidoProcesado = false;
        double descuentoCupon = 1.0;

        if (!esTotem) {
            System.out.print("¿Desea ingresar un cupón de descuento? (s/n): ");
            String resp = scanner.nextLine();
            if (resp.equalsIgnoreCase("s")) {
                System.out.print("Ingrese código del cupón: ");
                String cupon = scanner.nextLine().toUpperCase();
                if (cuponesValidos.contains(cupon)) {
                    System.out.println("Cupón válido aplicado: 10% de descuento adicional.");
                    descuentoCupon = 0.9;
                } else {
                    System.out.println("Cupón inválido.");
                }
            }
        } else {
            System.out.println("[INFO] Tótem: No se admite validación de cupones ni notificaciones a empleados.");
        }

        while (continuar) {
            System.out.println("\n1 - Ver productos disponibles");
            System.out.println("2 - Agregar producto al pedido");
            System.out.println("3 - Quitar producto del pedido");
            System.out.println("4 - Ver tiempo estimado");
            System.out.println("5 - Confirmar pedido");
            System.out.println("6 - Cancelar pedido");
            System.out.println("7 - Programar pedido");
            System.out.println("8 - Ver estado del pedido");
            System.out.println("0 - Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    mostrarCatalogo();
                    break;
                case 2:
                    mostrarCatalogo();
                    System.out.print("Ingrese número de producto a agregar: ");
                    int idxAgregar = scanner.nextInt();
                    scanner.nextLine();
                    if (idxAgregar >= 0 && idxAgregar < catalogo.size()) {
                        try {
                            pedido.agregarProducto(catalogo.get(idxAgregar));
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    break;
                case 3:
                    List<Producto> productos = pedido.getProductos();
                    if (productos.isEmpty()) {
                        System.out.println("No hay productos en el pedido.");
                        break;
                    }
                    for (int i = 0; i < productos.size(); i++) {
                        Producto p = productos.get(i);
                        System.out.println(i + ". " + p.getNombre());
                    }
                    System.out.print("Seleccione el índice del producto a quitar: ");
                    int quitar = scanner.nextInt();
                    scanner.nextLine();
                    if (quitar >= 0 && quitar < productos.size()) {
                        productos.remove(quitar);
                        System.out.println("Producto eliminado del pedido.");
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    break;
                case 4:
                    int tiempoEst = pedido.calcularTiempoEstimado(pedidosActivos.size(), esDelivery ? tiempoDelivery : 0);
                    System.out.println("Tiempo estimado: " + tiempoEst + " minutos");
                    break;
                case 5:
                    if (pedido.getProductos().isEmpty()) {
                        System.out.println("No se puede confirmar: el pedido está vacío.");
                        break;
                    }
                    System.out.println("\nResumen del pedido:");
                    double subtotal = 0;
                    for (Producto p : pedido.getProductos()) {
                        System.out.println("- " + p.getNombre() + " ($" + p.getPrecio() + ")");
                        subtotal += p.getPrecio();
                    }
                    System.out.println("Subtotal: $" + subtotal);
                    if (descuentoCupon < 1.0) {
                        System.out.println("Descuento por cupón: -" + (int)((1 - descuentoCupon) * 100) + "%");
                    }
                    if (pedido.getMetodoPago() == MetodoPago.EFECTIVO) {
                        System.out.println("Descuento por efectivo: -10%");
                    }
                    try {
                        double total = pedido.procesarPago();
                        total *= descuentoCupon;
                        pedidosActivos.add(pedido);
                        pedidoProcesado = true;
                        System.out.println("\nTotal a pagar: $" + total);
                        System.out.println("[INFO] Pedido confirmado y registrado.");
                    } catch (Exception e) {
                        System.out.println("Error al confirmar el pedido: " + e.getMessage());
                    }
                    break;
                case 6:
                    try {
                        System.out.println("Reembolso: $" + pedido.cancelar());
                        pedidosActivos.remove(pedido);
                        pedidoProcesado = false;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                case 7:
                    System.out.print("Ingrese fecha y hora (yyyy-MM-dd HH:mm): ");
                    try {
                        Date fecha = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm").parse(scanner.nextLine());
                        pedido.programarPara(fecha);
                        System.out.println("Pedido programado exitosamente.");
                    } catch (Exception e) {
                        System.out.println("Formato inválido.");
                    }
                    break;
                case 8:
                    System.out.println("Estado actual del pedido: " + pedido.getEstado());
                    break;
                case 0:
                    continuar = false;
                    if (!pedidoProcesado && pedido.getEstado() == EstadoPedido.EN_ESPERA) {
                        System.out.println("[AVISO] Pedido descartado sin ser procesado.");
                    }
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }


    public static void modoRestaurante(Scanner scanner) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1 - Ver productos actuales");
            System.out.println("2 - Agregar producto al menú");
            System.out.println("3 - Eliminar producto del menú");
            System.out.println("4 - Establecer tiempo de delivery");
            System.out.println("0 - Volver al menú principal");

            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1:
                    mostrarCatalogo();
                    break;
                case 2:
                    System.out.print("Nombre del producto: ");
                    String nombre = scanner.nextLine();
                    System.out.print("Precio: ");
                    double precio = scanner.nextDouble();
                    System.out.print("Tiempo de preparación (min): ");
                    int tiempo = scanner.nextInt();
                    scanner.nextLine();
                    catalogo.add(new Producto(nombre, precio, tiempo));
                    System.out.println("Producto agregado al menú.");
                    break;
                case 3:
                    mostrarCatalogo();
                    System.out.print("Índice de producto a eliminar: ");
                    int idx = scanner.nextInt();
                    scanner.nextLine();
                    if (idx >= 0 && idx < catalogo.size()) {
                        catalogo.remove(idx);
                        System.out.println("Producto eliminado.");
                    } else {
                        System.out.println("Índice inválido.");
                    }
                    break;
                case 4:
                    System.out.print("Ingrese nuevo tiempo de delivery en minutos: ");
                    tiempoDelivery = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Tiempo de delivery actualizado a " + tiempoDelivery + " minutos.");
                    break;
                case 0:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    public static void mostrarCatalogo() {
        if (catalogo.isEmpty()) {
            System.out.println("No hay productos en el menú.");
        } else {
            for (int i = 0; i < catalogo.size(); i++) {
                Producto p = catalogo.get(i);
                System.out.println(i + "  " + p.getNombre() + ": $" + p.getPrecio() + " - " + p.getTiempoPreparacion() + " min");
            }
        }
    }
}
