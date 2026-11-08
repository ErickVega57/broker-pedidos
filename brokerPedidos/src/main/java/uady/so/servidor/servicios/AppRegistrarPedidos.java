package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.servidor.ServidorProxy;
import uady.so.modelo.MVC.RegistrarPedido;

public class AppRegistrarPedidos {
    public static void main(String[] args) {

        PedidoRepository repositorio = new PedidoRepositoryCSV();
        RegistrarPedido registrarPedidoMVC = new RegistrarPedido(repositorio);
        ServicioRegistrarPedido registrarPedido = new ServicioRegistrarPedido(registrarPedidoMVC);

        // se crea el proxy con el servicio implementado.
        ServidorProxy proxy = new ServidorProxy(
                "consultarPedido", "localhost", 9000,
                "localhost", 9002, registrarPedido
        );
        proxy.iniciar();
    }
}
