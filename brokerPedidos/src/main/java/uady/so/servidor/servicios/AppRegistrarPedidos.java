package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.servidor.ServidorProxy;
import uady.so.modelo.MVC.RegistrarPedido;

public class AppRegistrarPedidos {
    public static void main(String[] args) {

        int puertoServicio = 9002;

        String hostBroker = "localhost";
        int puertoBroker = 9000;

        if(args.length != 0){
            puertoServicio = Integer.parseInt(args[0]);
            String[] partes = args[1].split(":");

            hostBroker = partes[0];
            puertoBroker = Integer.parseInt(partes[1]);
        }

        PedidoRepository repositorio = new PedidoRepositoryCSV();
        RegistrarPedido registrarPedidoMVC = new RegistrarPedido(repositorio);
        ServicioRegistrarPedido registrarPedido = new ServicioRegistrarPedido(registrarPedidoMVC);

        // se crea el proxy con el servicio implementado.
        ServidorProxy proxy = new ServidorProxy(
                "registrarPedido", hostBroker, puertoBroker,
                "localhost", puertoServicio, registrarPedido
        );
        proxy.iniciar();
    }
}
