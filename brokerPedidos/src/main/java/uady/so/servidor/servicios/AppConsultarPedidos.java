package uady.so.servidor.servicios;

import uady.so.datos.PedidoRepository;
import uady.so.datos.PedidoRepositoryCSV;
import uady.so.servidor.ServidorProxy;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class AppConsultarPedidos {
    public static void main(String[] args) {

        int puertoServicio = 9001;

        String hostBroker = "localhost";
        int puertoBroker = 9000;

        if(args.length != 0){
            puertoServicio = Integer.parseInt(args[0]);
            String[] partes = args[1].split(":");

            hostBroker = partes[0];
            puertoBroker = Integer.parseInt(partes[1]);
        }
        
        PedidoRepositoryCSV pedidoRepository = new PedidoRepositoryCSV();
        ServicioConsultarPedido consultarPedido = new ServicioConsultarPedido(pedidoRepository);

        // se crea el proxy con el servicio implementado.
        ServidorProxy proxy = new ServidorProxy(
          "consultarPedido", hostBroker, puertoBroker,
                "192.168.1.204", puertoServicio, consultarPedido
        );

        proxy.iniciar();
    }
}
