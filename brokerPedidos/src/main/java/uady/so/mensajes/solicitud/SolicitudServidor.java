package uady.so.mensajes.solicitud;

import java.util.ArrayList;
import java.util.LinkedHashMap;

 /*
  * Clase para crear solicitudes basicas que recibe el servidor
  * Solicitudes al servidor:
  *  - ListarPedidos
  *  - consultarPedidos
  *  - registrarPedido
  *  
  */

public class SolicitudServidor extends Solicitud {

    protected SolicitudServidor(String nombreServicio) {
        super(nombreServicio);
    }

    public static SolicitudServidor listarPedidos(){
        return new SolicitudServidor("listarPedidos");
    }

    public static SolicitudServidor consultarPedido(int id){
        SolicitudServidor solicitudServidor = new SolicitudServidor("consultarPedido");
        solicitudServidor.agregarParametro("id", id);
        return solicitudServidor;
    }

    public static SolicitudServidor registrarPedido(String cliente){
        SolicitudServidor solicitudServidor = new SolicitudServidor("registrarPedido");
        solicitudServidor.agregarParametro("cliente", cliente);
        solicitudServidor.agregarParametro("productos", new ArrayList<LinkedHashMap<String, Object>>());
        return solicitudServidor;
    }

    public ArrayList<LinkedHashMap<String, Object>> getProductos() {
        Object productos = getParametro("productos");
        if (productos instanceof ArrayList<?>) {
            return (ArrayList<LinkedHashMap<String, Object>>) productos;
        }
        return new ArrayList<>();
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

    public String getCliente(){
        return (String)getParametro("cliente");
    }

    public int getIdBuscado(){
        return (int)getParametro("id");
    }
}
