package uady.so.servidor.servicios;

import uady.so.json.MensajesJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudServidor;
import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ServicioListarPedidos implements Servicio {

    private final PedidoRepository repositorio;

    public ServicioListarPedidos() {
        this.repositorio = new PedidoRepositoryCSV();
    }

    @Override
    public Respuesta ejecutar(SolicitudServidor solicitud) {
        try {
            List<Pedido> pedidos = repositorio.obtenerTodos();

            return construirRespuesta(pedidos);
        } catch (Exception e) {
            // Manejo de error estandarizado por el proyecto
            return Respuesta.respuestaERROR(
                    "listarPedidos",
                    "Error al listar los pedidos: " + e.getMessage()
            );
        }
    }

    private Respuesta construirRespuesta(List<Pedido> pedidos) {

        // Creamos la respuesta OK usando el método estático del proyecto
        Respuesta respuesta = Respuesta.respuestaOK("listarPedidos");

        List<LinkedHashMap<String, Object>> pedidosLista = new ArrayList<>();
        for(Pedido pedido : pedidos) {
            LinkedHashMap<String, Object> pedidoMap = new LinkedHashMap<>();
            pedidoMap.put("id", pedido.getId());
            pedidoMap.put("cliente", pedido.getCliente());

            List<LinkedHashMap<String, Object>> productosRespuesta = new ArrayList<>();
            for (Producto p : pedido.getListaDeProductos()) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("nombre", p.getNombreDeProducto());
                map.put("precio", p.getPrecioDeProducto());
                map.put("cantidad", p.getCantidadSolicitada());
                map.put("existencia", p.getExistencia());
                productosRespuesta.add(map);
            }
            pedidoMap.put("productos", productosRespuesta);
            pedidoMap.put("subtotal", pedido.getSubtotal());
            pedidoMap.put("descuento", pedido.getDescuento());
            pedidoMap.put("impuestos", pedido.getImpuestos());
            pedidoMap.put("total", pedido.getTotal());
            pedidoMap.put("estado", pedido.getEstado().name());
            pedidosLista.add(pedidoMap);
        }
        respuesta.agregarComoResultado(pedidosLista);
        return respuesta;
    }

    public static void main(String[] args) {
        ServicioListarPedidos servicio = new ServicioListarPedidos();

        MensajesJsonAdapter jsonAdapter = new MensajesJsonAdapter();
        System.out.println(jsonAdapter.serializar(servicio.ejecutar(SolicitudServidor.listarPedidos())));
    }

}