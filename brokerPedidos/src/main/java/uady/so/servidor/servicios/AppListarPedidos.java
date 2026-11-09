package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.modelo.MVC.RegistrarPedido;
import uady.so.servidor.ServidorProxy;

public class AppListarPedidos {
    public static void main(String[] args) {
        int puertoServicio = 9003;

        String hostBroker = "localhost";
        int puertoBroker = 9000;

        if(args.length != 0){
            puertoServicio = Integer.parseInt(args[0]);
            String[] partes = args[1].split(":");

            hostBroker = partes[0];
            puertoBroker = Integer.parseInt(partes[1]);
        }

        ServicioListarPedidos listarPedidos = new ServicioListarPedidos();

        // se crea el proxy con el servicio implementado.
        ServidorProxy proxy = new ServidorProxy(
                "listarPedidos", hostBroker, puertoBroker,
                "192.168.1.204", puertoServicio, listarPedidos
        );
        proxy.iniciar();
    }
}
