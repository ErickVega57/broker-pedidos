package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.excepciones.PedidoNoEncontrado;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudServidor;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ServicioConsultarPedido implements Servicio{
    private final PedidoRepository repositorio;

    public ServicioConsultarPedido(PedidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Respuesta ejecutar(SolicitudServidor solicitud) {
        Respuesta respuesta;

        // id del pedido buscado
        int id = solicitud.getIdBuscado();
        
        try {
            // si el pedido no se ecuentra envia excepcion
            // Pedido no encontrado
            Pedido pedido = repositorio.consultar(id);

            // se crea una respuesta de OK  del servicio "consultarPedido"
            respuesta = Respuesta.respuestaOK("consultarPedido");
            respuesta.agregarResultado("id", id);
            respuesta.agregarResultado("cliente", pedido.getCliente());

            // hasmap para meter los productos a la espuesta
            List<LinkedHashMap<String, Object>> productos = new ArrayList<>();

            for(Producto producto : pedido.getListaDeProductos()){
                LinkedHashMap<String, Object> productoMap = new LinkedHashMap<>();
                productoMap.put("nombre", producto.getNombreDeProducto());
                productoMap.put("precio", producto.getPrecioDeProducto());
                productoMap.put("cantidad", producto.getCantidadSolicitada());
                productoMap.put("existencia", producto.getExistencia());
                productos.add(productoMap);
            }
            // agregamos los productos
            respuesta.agregarResultado("productos", productos);

            // agregamos los demas resultados del pedido
            respuesta.agregarResultado("subtotal", pedido.getSubtotal());
            respuesta.agregarResultado("descuento", pedido.getDescuento());
            respuesta.agregarResultado("impuestos", pedido.getImpuestos());
            respuesta.agregarResultado("total", pedido.getTotal());
            respuesta.agregarResultado("estado", pedido.getEstado());

        } catch (PedidoNoEncontrado e){
            // si el pedido no se encuentra se captura la excepcion
            // se crea respuestaERROR
            respuesta = Respuesta.respuestaERROR("consultarPedido", e.getMessage());
        }

        return respuesta;
    }
}
