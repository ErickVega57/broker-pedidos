package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.servidor.ServidorProxy;

public class AppConsultarPedidos {
    public static void main(String[] args) {
        PedidoRepositoryCSV pedidoRepository = new PedidoRepositoryCSV();
        ServicioConsultarPedido consultarPedido = new ServicioConsultarPedido(pedidoRepository);
        ServidorProxy proxy = new ServidorProxy(
          "consultarPedido", "localhost", 9000,
                "localhost", 9001, consultarPedido
        );
        proxy.iniciar();
    }
}
