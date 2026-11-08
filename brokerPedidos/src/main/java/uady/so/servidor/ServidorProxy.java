package uady.so.servidor;

import uady.so.json.SolicitudJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudServidor;
import uady.so.servidor.servicios.Servicio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServidorProxy {
    private final SolicitudJsonAdapter solicitudJsonAdapter = new SolicitudJsonAdapter();

    private final String nombreServicio;
    private final String hostBroker;
    private final int puertoBroker;
    private final String hostSerivicio;
    private final int puertoServicio;
    private Servicio servicio;


    public ServidorProxy(
            String nombreServicio, String hostBroker, int puertoBroker,
            String hostSerivicio, int puertoServicio, Servicio servicio
    ) {
        this.nombreServicio = nombreServicio;
        this.hostBroker = hostBroker;
        this.puertoBroker = puertoBroker;
        this.hostSerivicio = hostSerivicio;
        this.puertoServicio = puertoServicio;
        this.servicio = servicio;
    }

    public void iniciar(){
        registrarEnBroker();

        try(ServerSocket servidor = new ServerSocket(puertoServicio)) {
            System.out.println("Servicio en escucha en puerto:" + puertoServicio);
            while (true) {
                try (
                        Socket socket = servidor.accept();
                        OutputStreamWriter salida = new OutputStreamWriter(
                                socket.getOutputStream(), StandardCharsets.UTF_8
                        );
                        BufferedReader entrada = new BufferedReader(
                                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                        );
                ) {
                    String jsonSolicitud = entrada.readLine();
                    System.out.println(jsonSolicitud);
                    SolicitudServidor solicitudServidor =
                            solicitudJsonAdapter.deserializarSolicitudServidor(jsonSolicitud);

                    Respuesta respuesta = servicio.ejecutar(solicitudServidor);
                    System.out.println(solicitudJsonAdapter.serializar(respuesta));

                    salida.write(solicitudJsonAdapter.serializar(respuesta));
                    salida.flush();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void registrarEnBroker() {
        SolicitudBroker solicitudRegistro =
                SolicitudBroker.SolicitudRegistrar(
                nombreServicio,
                hostSerivicio,
                puertoServicio
        );
        try(
                Socket socket = new Socket(hostBroker, puertoBroker);
                OutputStreamWriter salida = new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8
                );
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
        ){
            String jsonSolicitud = solicitudJsonAdapter.serializar(solicitudRegistro);

            salida.write(jsonSolicitud + "\n");
            salida.flush();

            String jsonRespuesta = entrada.readLine();
            System.out.println(jsonRespuesta);


        } catch (Exception e){
            Respuesta respuesta = Respuesta.respuestaERROR(
                    "registrar",
                    "No se pudo registrar " + nombreServicio + " al broker " + e.getMessage()
            );
            System.out.println(solicitudJsonAdapter.serializar(respuesta));
        }
    }
}

