package uady.so.cliente;

import uady.so.json.MensajesJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.Solicitud;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;
import uady.so.modelo.Pedido;

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
        
        // iniciamos el proxy creamos solicitudes, las enviamos al broker 
        // y recibimos respuestas con el metodo invocar del proxy.
        ClienteProxy proxy = new ClienteProxy(hostBroker, puertoBroker);
        MensajesJsonAdapter mensajesJsonAdapter = new MensajesJsonAdapter();

        //SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudListarTodos();
        //String jsonRespuesta = proxy.invocar(solicitudBroker);

        //SolicitudBroker solicitudBroker1 = SolicitudBroker.SolicitudBuscar("registrar");
        //String jsonRespuesta2 = proxy.invocar(solicitudBroker1);


        System.out.println("PRUEBAS EXITOS");pruebasExitos(proxy, mensajesJsonAdapter);

        //System.out.println("PRUEBAS ERRORES");pruebasErrores(proxy, mensajesJsonAdapter);


    }

    public static void pruebasExitos(ClienteProxy proxy, MensajesJsonAdapter mensajesJsonAdapter ){
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
    public static void pruebasErrores(ClienteProxy proxy, MensajesJsonAdapter mensajesJsonAdapter){

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
}
