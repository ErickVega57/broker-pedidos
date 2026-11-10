package uady.so.cliente;

import uady.so.json.MensajesJsonAdapter;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;

import java.util.LinkedHashMap;

public class Cliente {

    public static void main(String[] args) {
        String hostBroker = "localhost";
        int puertoBroker = 9000;

        if(args.length != 0){
            String[] partes = args[0].split(":");

            hostBroker = partes[0];
            puertoBroker = Integer.parseInt(partes[1]);
        }

        //System.out.println("PRUEBAS EXITOS");pruebasExitos(proxy, mensajesJsonAdapter);

        //System.out.println("PRUEBAS ERRORES");pruebasErrores(proxy, mensajesJsonAdapter);
        ClienteProxy proxy = new ClienteProxy(hostBroker, puertoBroker);
        VistaCliente vista = new VistaCliente();
        Cliente cliente = new Cliente(vista, proxy);

        cliente.iniciar();

    }

    public void pruebasExitos(){
        SolicitudBrokerPedidos registrarPedido = SolicitudBrokerPedidos.registarPedido("Ana López");
        registrarPedido.agregarProducto(
                "Producto A",
                600.0,
                2,
                10
                );
        SolicitudBrokerPedidos consultarPedido = SolicitudBrokerPedidos.consultarPedido(1);
        SolicitudBrokerPedidos listarPedidos = SolicitudBrokerPedidos.listarPedidos();

        String respuestaRegistroPedido = proxy.invocar(registrarPedido);
        String respuestaConsultarPedido = proxy.invocar(consultarPedido);
        String respuestaListarPedidos = proxy.invocar(listarPedidos);

        String solicitudRegistrarPedido = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(registrarPedido));
        String solicitudConsultarPedido = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(consultarPedido));
        String solicitudListarPedido = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(listarPedidos));

        System.out.println(solicitudRegistrarPedido);
        System.out.println(mensajesJsonAdapter.formatear(respuestaRegistroPedido));

        System.out.println(solicitudConsultarPedido);
        System.out.println(mensajesJsonAdapter.formatear(respuestaConsultarPedido));

        System.out.println(solicitudListarPedido);
        System.out.println(mensajesJsonAdapter.formatear(respuestaListarPedidos));

    }
    public void pruebasErrores(){

        SolicitudBroker servicioInexistente =
                SolicitudBroker.SolicitudEjecutar("servicioInexistente", new LinkedHashMap<>());

        SolicitudBrokerPedidos consultarPedido = SolicitudBrokerPedidos.consultarPedido(90);

        SolicitudBrokerPedidos registrarPedido = SolicitudBrokerPedidos.registarPedido("Ana López");
        registrarPedido.agregarProducto(
                "Producto A",
                600.0,
                20,
                10
        );

        String respuestaRegistroPedido = proxy.invocar(registrarPedido);
        String respuestaConsultarPedido = proxy.invocar(consultarPedido);
        String respuestaServicioInexistente = proxy.invocar(servicioInexistente);

        String solicitudRegistrarPedido = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(registrarPedido));
        String solicitudConsultarPedido = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(consultarPedido));
        String solicitudServicioInexistente = mensajesJsonAdapter.formatear(mensajesJsonAdapter.serializar(servicioInexistente));

        System.out.println(solicitudRegistrarPedido);
        System.out.println(mensajesJsonAdapter.formatear(respuestaRegistroPedido));

        System.out.println(solicitudConsultarPedido);
        System.out.println(mensajesJsonAdapter.formatear(respuestaConsultarPedido));

        System.out.println(solicitudServicioInexistente);
        System.out.println(mensajesJsonAdapter.formatear(respuestaServicioInexistente));

    }

    private final VistaCliente vista;
    private final ClienteProxy proxy;
    private final MensajesJsonAdapter mensajesJsonAdapter = new MensajesJsonAdapter();

    Cliente(VistaCliente vistaCliente, ClienteProxy proxy){
        this.vista = vistaCliente;
        this.proxy = proxy;
    }

    private void ejecutarOpciones(int opcion){
        switch (opcion) {
            case 1:
                vista.mostrarRespuesta(registrarPedido());
                break;
            case 2:
                vista.mostrarRespuesta(consultarPedido());
                break;
            case 3:
                vista.mostrarRespuesta(listarPedidos());
                break;
            case 4:
                vista.mostrarRespuesta(buscar());
                break;
            case 5:
                vista.mostrarRespuesta(listarTodos());
                break;
            case 6:
                System.out.println("Cerrando el programa...");
                break;
            default:
                System.out.println("Opción inválida. Intenta de nuevo.");
        }
    }

    public void iniciar(){
        int opcion;
        do {
            try{
                opcion = vista.mostrarMenu();
            } catch (NumberFormatException e) {
                opcion = 0;
            }
            ejecutarOpciones(opcion);
        }while(opcion != 6);
    }

    public String registrarPedido(){
        try {

            SolicitudBroker solicitudBroker = vista.registrarPedido();
            return mensajesJsonAdapter.formatear(proxy.invocar(solicitudBroker));

        }catch (Exception e){
            return "Error al ingresar el pedido";
        }
    }
    public String consultarPedido(){
        try{

            SolicitudBroker solicitudBroker = vista.consultarPedido();
            return mensajesJsonAdapter.formatear(proxy.invocar(solicitudBroker));

        } catch (Exception e) {
            return "Ingrese un id válido";
        }

    }

    public String listarPedidos(){
        SolicitudBroker solicitudBroker = SolicitudBrokerPedidos.listarPedidos();
        return mensajesJsonAdapter.formatear(proxy.invocar(solicitudBroker));
    }

    public String buscar(){
        try {
            String servicio = vista.buscarServicio();
            SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudBuscar(servicio);
            return mensajesJsonAdapter.formatear(proxy.invocar(solicitudBroker));
        } catch (Exception e) {
            return "Error al buscar " + e.getMessage();
        }
    }

    public String listarTodos(){
        SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudListarTodos();
        return mensajesJsonAdapter.formatear(proxy.invocar(solicitudBroker));
    }

}
