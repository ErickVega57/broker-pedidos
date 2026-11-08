package uady.so.mensajes.solicitud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SolicitudBrokerPedidos extends SolicitudBroker{
    /*
     * Solicitudes de Pedidos al broker:
     *  - RegistrarPedido
     *  - ConsultarPedido
     *  - ListarPedidos
     */
    private final LinkedHashMap<String,Object> args;

    private SolicitudBrokerPedidos(String nombreServicio) {
        super("ejecutar");
        this.args = new LinkedHashMap<>();

        this.agregarParametro("servicio", nombreServicio);
        this.agregarParametro("argumentos", this.args);
    }
    public static SolicitudBrokerPedidos listarPedidos(){
        return new SolicitudBrokerPedidos("listarPedidos");
    }

    public static SolicitudBrokerPedidos consultarPedido(int id){
        SolicitudBrokerPedidos solicitudBrokerPedidos =
                new SolicitudBrokerPedidos("consultarPedido");
        solicitudBrokerPedidos.agregarArgumento("id", id);
        return solicitudBrokerPedidos;
    }

    public static SolicitudBrokerPedidos registarPedido(String cliente){
        SolicitudBrokerPedidos solicitudBrokerPedidos
                = new SolicitudBrokerPedidos("registrarPedido");
        solicitudBrokerPedidos.agregarArgumento("cliente", cliente);
        solicitudBrokerPedidos.agregarArgumento("productos", new ArrayList<LinkedHashMap<String,Object>>());
        return solicitudBrokerPedidos;
    }

    public ArrayList<LinkedHashMap<String, Object>> getProductos() {
        Object productos = args.get("productos");
        if (productos instanceof ArrayList<?>) {
            return (ArrayList<LinkedHashMap<String, Object>>) productos;
        }
        return new ArrayList<>();
    }

    private void agregarArgumento(String clave, Object valor){
        args.put(clave, valor);
    }

    public void agregarProducto(String nombre, double precio, int cantidad, int existencia){
        ArrayList<LinkedHashMap<String,Object>> productos = getProductos();

        LinkedHashMap<String,Object> producto = new LinkedHashMap<>();
        producto.put("nombre", nombre);
        producto.put("precio", precio);
        producto.put("cantidad", cantidad);
        producto.put("existencia", existencia);
        productos.add(producto);
    }

    public int getId(){
        return (int)args.get("id");
    }
    public String getCliente(){
        return (String)args.get("cliente");
    }
    public String getServicioSolicitado(){
        return (String)getParametro("servicio");
    }

}
