package uady.so;

import uady.so.datos.PedidoRepositoryCSV;
import uady.so.json.MensajesJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        MensajesJsonAdapter mensajesJsonAdapter = new MensajesJsonAdapter();

        SolicitudBroker listarTodos =
                SolicitudBroker.SolicitudListarTodos();
        SolicitudBroker buscar =
                SolicitudBroker.SolicitudBuscar("facturacion");
        SolicitudBroker registrar =
                SolicitudBroker.SolicitudRegistrar(
                        "facturacion",
                        "127.000.007",
                        9001
                );

        System.out.println(mensajesJsonAdapter.serializar(listarTodos));
        System.out.println(mensajesJsonAdapter.serializar(buscar));
        System.out.println(mensajesJsonAdapter.serializar(registrar));

        SolicitudBrokerPedidos registrarPedido = SolicitudBrokerPedidos.registarPedido("Erick");
        registrarPedido.agregarProducto(
                "cepillo",
                35.50,
                3,
                10
        );

        SolicitudBrokerPedidos consultarPedido = SolicitudBrokerPedidos.consultarPedido(1);
        SolicitudBrokerPedidos listarPedidos = SolicitudBrokerPedidos.listarPedidos();

        System.out.println(mensajesJsonAdapter.serializar(registrarPedido));
        System.out.println(mensajesJsonAdapter.serializar(consultarPedido));
        System.out.println(mensajesJsonAdapter.serializar(listarPedidos));


        Respuesta respuestaError = Respuesta.respuestaERROR(
                "listar",
                "No se puede hacer eso"
        );

        Respuesta respuestaOk = Respuesta.respuestaOK("nombreServicio");

        System.out.println(mensajesJsonAdapter.serializar(respuestaError));
        System.out.println(mensajesJsonAdapter.serializar(respuestaOk));

        System.out.println("==    DESERIALIZACION     ==");

        SolicitudBroker solicitudBroker = SolicitudBroker.SolicitudBuscar("Consultar");
        String jsonPrueba = mensajesJsonAdapter.serializar(solicitudBroker);

        SolicitudBroker solicitudBroker1 = mensajesJsonAdapter.deserializarSolicitudBroker(jsonPrueba);
        System.out.println(solicitudBroker1.getSolicitudTipo());
        System.out.println(solicitudBroker1.getServicioBuscado());
        System.out.println(mensajesJsonAdapter.serializar(solicitudBroker1));

        String jsonListar = mensajesJsonAdapter.serializar(listarPedidos);
        SolicitudBroker solicitudBroker2 = mensajesJsonAdapter.deserializarSolicitudBroker(jsonListar);
        System.out.println(solicitudBroker2.getSolicitudTipo());
        System.out.println(solicitudBroker2.getServicioBuscado());
        System.out.println(mensajesJsonAdapter.serializar(solicitudBroker2));

        String jsonRegistrar = mensajesJsonAdapter.serializar(registrar);
        SolicitudBroker solicitudBroker3 = mensajesJsonAdapter.deserializarSolicitudBroker(jsonRegistrar);
        System.out.println(solicitudBroker3.getSolicitudTipo());
        System.out.println(solicitudBroker3.getNombreServicio());
        System.out.println(solicitudBroker3.getIp());
        System.out.println(solicitudBroker3.getPuerto());
        System.out.println(mensajesJsonAdapter.serializar(solicitudBroker3));

        String jsonRegistrarPedido = mensajesJsonAdapter.serializar(registrarPedido);

        // registrar pedido
        SolicitudBrokerPedidos solicitudBroker4 =
                (SolicitudBrokerPedidos) mensajesJsonAdapter.deserializarSolicitudBroker(jsonRegistrarPedido);

        System.out.println(jsonRegistrarPedido);
        System.out.println(solicitudBroker4.getSolicitudTipo());
        System.out.println(solicitudBroker4.getServicioSolicitado());
        System.out.println(solicitudBroker4.getCliente());

        ArrayList<LinkedHashMap<String, Object>> productos = solicitudBroker4.getProductos();

        int contador = 1;
        for (LinkedHashMap<String, Object> producto : productos) {
            System.out.println("--- Producto #" + contador + " ---");

            // Recorremos cada propiedad dentro del LinkedHashMap
            for (Map.Entry<String, Object> propiedad : producto.entrySet()) {
                String clave = propiedad.getKey();
                Object valor = propiedad.getValue();
                System.out.println(clave + ": " + valor);
            }
            System.out.println(); // Salto de línea entre pedidos
            contador++;
        }

        String jsonresp = "{\"servicio\":\"consultarPedido\",\"estado\":\"ERROR\",\"mensaje\":\"No se ha encontrado el pedido\"}";
        Respuesta respuesta = mensajesJsonAdapter.deserializarRespuesta(jsonresp);


        List<Producto> productosLista = List.of(
                new Producto(
                        "lapiz", 100, 4, 7
                )
        );
        Pedido pedido = new Pedido("PRUEBA",productosLista);
        PedidoRepositoryCSV pedidoRepository = new PedidoRepositoryCSV();
        pedidoRepository.guardar(pedido);

    }
}