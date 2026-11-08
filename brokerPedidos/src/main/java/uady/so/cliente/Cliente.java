package uady.so.cliente;

import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;
import uady.so.modelo.Pedido;

public class Cliente {
    public static void main(String[] args) {
        ClienteProxy proxy = new ClienteProxy("localhost", 9000);

        SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudListarTodos();
        String jsonRespuesta = proxy.invocar(solicitudBroker);

        SolicitudBroker solicitudBroker1 = SolicitudBroker.SolicitudBuscar("registrar");
        String jsonRespuesta2 = proxy.invocar(solicitudBroker1);

        SolicitudBrokerPedidos solicitudDePedido = SolicitudBrokerPedidos.consultarPedido(3);
        String jsonRespuesta3 = proxy.invocar(solicitudDePedido);

        System.out.println(jsonRespuesta);
        System.out.println(jsonRespuesta2);
        System.out.println(jsonRespuesta3);
    }
}
