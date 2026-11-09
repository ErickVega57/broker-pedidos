package uady.so.cliente;

import uady.so.json.MensajesJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudBroker;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClienteProxy {

    private final MensajesJsonAdapter mensajesJsonAdapter = new MensajesJsonAdapter();
    private final String hostBroker;
    private final int puertoBroker;

    public ClienteProxy(String hostBroker, int puertoBroker) {
        this.hostBroker = hostBroker;
        this.puertoBroker = puertoBroker;
    }

    // método para invocar al broker recibir una respuesta.
    public String invocar(SolicitudBroker solicitudBroker){
        // inicia conexión
        try(
                Socket socket = new Socket(hostBroker, puertoBroker);
                OutputStreamWriter salida = new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8
                );
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
        ){
            // volvemos la solicitud String json y enviamos
            String jsonSolicitud = mensajesJsonAdapter.serializar(solicitudBroker);
    
            salida.write(jsonSolicitud + "\n");
            salida.flush();

            // retornamos la respuesta
            return entrada.readLine();

        } catch (Exception e){
            Respuesta respuesta = Respuesta.respuestaERROR(
                    "ejecutar",
                    "No fue posible posible conectarse al broker"
            );
            return mensajesJsonAdapter.serializar(respuesta);
        }
    }
}
