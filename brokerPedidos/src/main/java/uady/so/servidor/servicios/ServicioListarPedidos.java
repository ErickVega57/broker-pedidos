package uady.so.servidor.servicios;

import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudServidor;
import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.modelo.Pedido;

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

            // Creamos la respuesta OK usando el método estático del proyecto
            Respuesta respuesta = Respuesta.respuestaOK("listarPedidos");
            respuesta.agregarResultado("pedidos", pedidos);

            return respuesta;
        } catch (Exception e) {
            // Manejo de error estandarizado por el proyecto
            return Respuesta.respuestaERROR(
                    "listarPedidos",
                    "Error al listar los pedidos: " + e.getMessage()
            );
        }
    }
}