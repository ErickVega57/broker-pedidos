package uady.so.cliente;

import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;
import uady.so.modelo.Pedido;

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

        SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudListarTodos();
        String jsonRespuesta = proxy.invocar(solicitudBroker);

        SolicitudBroker solicitudBroker1 = SolicitudBroker.SolicitudBuscar("registrar");
        String jsonRespuesta2 = proxy.invocar(solicitudBroker1);

        SolicitudBrokerPedidos solicitudDePedido = SolicitudBrokerPedidos.consultarPedido(1);
        String jsonRespuesta3 = proxy.invocar(solicitudDePedido);

        System.out.println(jsonRespuesta);
        System.out.println(jsonRespuesta2);
        System.out.println(jsonRespuesta3);


        // probar flujo registrar pedido y consultarlo
        SolicitudBrokerPedidos registrarPedido = SolicitudBrokerPedidos.registarPedido("Erick");
        registrarPedido.agregarProducto(
                "cepillo",
                35.50,
                3,
                10
        );
        String respuestaRegistrar = proxy.invocar(registrarPedido);


        System.out.println("RESPUESTA DEL REGISTRO");
        System.out.println(respuestaRegistrar);
    }
}
