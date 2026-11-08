package uady.so.cliente;

import uady.so.json.SolicitudJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudBroker;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteProxy {

    private final SolicitudJsonAdapter solicitudJsonAdapter = new SolicitudJsonAdapter();
    private final String hostBroker;
    private final int puertoBroker;

    public ClienteProxy(String hostBroker, int puertoBroker) {
        this.hostBroker = hostBroker;
        this.puertoBroker = puertoBroker;
    }

    public String invocar(SolicitudBroker solicitudBroker){
        try(
                Socket socket = new Socket(hostBroker, puertoBroker);
                OutputStreamWriter salida = new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8
                );
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
        ){
            String jsonSolicitud = solicitudJsonAdapter.serializar(solicitudBroker);

            salida.write(jsonSolicitud + "\n");
            salida.flush();

            return entrada.readLine();

        } catch (Exception e){
            Respuesta respuesta = Respuesta.respuestaERROR(
                    "ejecutar",
                    "No fue posible posible conectarse al broker"
            );
            return solicitudJsonAdapter.serializar(respuesta);
        }
    }
}
