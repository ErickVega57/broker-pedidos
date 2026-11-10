package uady.so.cliente;

import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

public class VistaCliente {
    private final Scanner scanner;

    public VistaCliente(){
        this.scanner = new Scanner(System.in);
    }

    public int mostrarMenu(){
        System.out.println("\n===== MENÚ PRINCIPAL =====");
        System.out.println("1. Registrar pedido");
        System.out.println("2. Consultar pedido por ID");
        System.out.println("3. Listar pedidos");
        System.out.println("4. Buscar un servicio");
        System.out.println("5. Listar servicios registrados");
        System.out.println("6. Salir");
        System.out.println("==========================");

        System.out.print("Selecciona una opción: ");
        return Integer.parseInt(scanner.nextLine());
    }


    public SolicitudBrokerPedidos registrarPedido() {
        System.out.println("\n--- REGISTRAR PEDIDO ---");

        System.out.print("Nombre del cliente: ");
        String cliente = scanner.nextLine();

        SolicitudBrokerPedidos registrarPedido =
                SolicitudBrokerPedidos.registarPedido(cliente);

        String continuar;

        //añadir productos al pedido
        do {
            System.out.print("Nombre del producto: ");
            String nombre = scanner.nextLine();

            System.out.print("Precio: ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("Cantidad: ");
            int cantidad = Integer.parseInt(scanner.nextLine());

            System.out.print("Elementos en existencia: ");
            int enExistencia = Integer.parseInt(scanner.nextLine());

            registrarPedido.agregarProducto(nombre, precio, cantidad, enExistencia);

            System.out.print("¿Agregar otro producto? (s/n): ");
            continuar = scanner.nextLine().trim().toLowerCase();

        } while (continuar.equals("s"));

        return registrarPedido;
    }

    protected SolicitudBrokerPedidos consultarPedido() {
        System.out.println("\n--- CONSULTAR PEDIDO ---");
        System.out.print("ID del pedido: ");
        int id = Integer.parseInt(scanner.nextLine());

        return SolicitudBrokerPedidos.consultarPedido(id);
    }

    public void mostrarRespuesta(String respuesta){
        System.out.println(respuesta);
    }

    public String buscarServicio() {
        System.out.println("\n--- BUSCAR SERVICIO ---");
        System.out.print("Nombre del servicio: ");
        return (scanner.nextLine());
    }
}
