package uady.so.servidor.servicios;

import uady.so.excepciones.PedidoException;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudServidor;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;
import uady.so.modelo.MVC.RegistrarPedido;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ServicioRegistrarPedido implements Servicio{

    private final RegistrarPedido registrarPedido;

    public ServicioRegistrarPedido(RegistrarPedido registrarPedido) {
        this.registrarPedido = registrarPedido;
    }

    @Override
    public Respuesta ejecutar(SolicitudServidor solicitud) {
        Respuesta respuesta;
        try {
            List<Producto> productos = new ArrayList<>();

            for (Map<String, Object> m : solicitud.getProductos()) {
                productos.add(new Producto(
                        (String) m.get("nombre"),
                        ((Number) m.get("precio")).doubleValue(),
                        ((Number) m.get("cantidad")).intValue(),
                        ((Number) m.get("existencia")).intValue()
                ));
            }
            Pedido pedido = new Pedido(solicitud.getCliente(), productos);

            registrarPedido.registrarPedido(pedido);

            respuesta = construirRespuesta(pedido);

            return respuesta;
        } catch (PedidoException e){
            respuesta = Respuesta.respuestaERROR("registrarPedido", e.getMessage());
        } catch (RuntimeException e){
            respuesta = Respuesta.respuestaERROR("registrarPedido", e.getMessage());
        }
        return respuesta;
    }

    private Respuesta construirRespuesta(Pedido pedido) {
        Respuesta respuesta = Respuesta.respuestaOK("registrarPedido");

        respuesta.agregarResultado("id",      pedido.getId());
        respuesta.agregarResultado("cliente", pedido.getCliente());

        List<LinkedHashMap<String, Object>> productosRespuesta = new ArrayList<>();
        for (Producto p : pedido.getListaDeProductos()) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put("nombre",     p.getNombreDeProducto());
            map.put("precio",     p.getPrecioDeProducto());
            map.put("cantidad",   p.getCantidadSolicitada());
            map.put("existencia", p.getExistencia());
            productosRespuesta.add(map);
        }
        respuesta.agregarResultado("productos", productosRespuesta);

        respuesta.agregarResultado("subtotal",  pedido.getSubtotal());
        respuesta.agregarResultado("descuento", pedido.getDescuento());
        respuesta.agregarResultado("impuestos", pedido.getImpuestos());
        respuesta.agregarResultado("total",     pedido.getTotal());
        respuesta.agregarResultado("estado",    pedido.getEstado().name());

        return respuesta;
    }

}
