package uady.so.broker;

import uady.so.excepciones.ServicioNoEncontrado;
import uady.so.json.SolicitudJsonAdapter;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;
import uady.so.mensajes.solicitud.SolicitudServidor;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Broker {
    private final SolicitudJsonAdapter solicitudJsonAdapter = new SolicitudJsonAdapter();
    private int puerto;

    private Map<Integer, DatosServicio>  servicios = new ConcurrentHashMap<>();
    private final AtomicInteger siguienteId;

    public Broker(int puerto) {
        this.puerto = puerto;
        this.siguienteId = new AtomicInteger(1);
    }

    public void iniciar(){
        try(ServerSocket servidor = new ServerSocket(puerto)){
            System.out.println("Broker en escucha en puerto:" + puerto);
            while (true){
                Socket socket = servidor.accept();
                new Thread(()->atender(socket)).start();
            }

        }catch (Exception e){
            System.out.println("No se pudo inciar el broker");
            e.printStackTrace();
        }
    }

    private void atender(Socket socket){
        try(
                Socket socket1 = socket;
                OutputStreamWriter salida = new OutputStreamWriter(
                        socket1.getOutputStream(), StandardCharsets.UTF_8
                );
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket1.getInputStream(), StandardCharsets.UTF_8)
                );
        ){
            String jsonSolicitud = entrada.readLine();
            System.out.println("Solicitud recibida");
            System.out.println(jsonSolicitud);
            SolicitudBroker solicitudBroker = solicitudJsonAdapter.deserializarSolicitudBroker(jsonSolicitud);


            Respuesta respuesta;
            if(Objects.equals(solicitudBroker.getSolicitudTipo(), "registrar")){
                respuesta = registrarServicio(solicitudBroker);
            } else if(Objects.equals(solicitudBroker.getSolicitudTipo(), "listar")){
                respuesta = listar(solicitudBroker);
            } else {
                try {
                    DatosServicio servicio = buscarServicio((SolicitudBrokerPedidos)solicitudBroker);
                    // respuesta del servidor
                    Respuesta respuestaServicio = invocar(servicio, (SolicitudBrokerPedidos) solicitudBroker);

                    if(respuestaServicio.getEstado().equals("OK")){
                        respuesta = Respuesta.respuestaOK(respuestaServicio);
                    } else {
                        respuesta = Respuesta.respuestaERROR(respuestaServicio);
                    }

                }catch (ServicioNoEncontrado e){
                    respuesta = Respuesta.respuestaERROR(
                            solicitudBroker.getSolicitudTipo(),
                            e.getMessage());
                }
            }
            System.out.println("aca");
            String jsonRespuesta = solicitudJsonAdapter.serializar(respuesta);

            salida.write(jsonRespuesta + "\n");
            salida.flush();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private Respuesta registrarServicio(SolicitudBroker solicitudBroker){
        Integer id = siguienteId.getAndIncrement();
        servicios.put(
                id,
                new DatosServicio(
                        solicitudBroker.getNombreServicio(),
                        solicitudBroker.getIp(),
                        solicitudBroker.getPuerto()
                )
        );
        System.out.println("Servicio " + solicitudBroker.getNombreServicio());
        System.out.println("Registrado en ->" +solicitudBroker.getIp() + ":" + solicitudBroker.getPuerto() );

        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());
        respuesta.agregarResultado("id", id);

        return respuesta;
    }

    private Respuesta listar(SolicitudBroker solicitudBroker){
        if(solicitudBroker.getServicioBuscado() != null){
            return buscar(solicitudBroker);
        }
        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());

        List<LinkedHashMap<String, String>> serviciosEncontrados = new ArrayList<>();
        for (DatosServicio servicio : servicios.values()) {
            LinkedHashMap<String, String> servicioMap = new LinkedHashMap<>();
            servicioMap.put("nombre", servicio.getNombreServicio());
            serviciosEncontrados.add(servicioMap);
        }
        respuesta.agregarComoResultado(serviciosEncontrados);
        return respuesta;
    }

    private Respuesta buscar(SolicitudBroker solicitudBroker) {
        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());

        List<LinkedHashMap<String, String>> serviciosEncontrados = new ArrayList<>();

        for (DatosServicio servicio : servicios.values()) {
            LinkedHashMap<String, String> servicioMap = new LinkedHashMap<>();

            if(servicio.getNombreServicio().contains(solicitudBroker.getServicioBuscado())) {
                servicioMap.put("nombre", servicio.getNombreServicio());
                serviciosEncontrados.add(servicioMap);
            }
        }
        respuesta.agregarComoResultado(serviciosEncontrados);

        if(serviciosEncontrados.isEmpty()){
            respuesta = Respuesta.respuestaERROR(
                    solicitudBroker.getSolicitudTipo(),
                    "Servicio no registrado");
        }
        return respuesta;
    }

    private DatosServicio buscarServicio(SolicitudBrokerPedidos solicitudBroker){
        for (DatosServicio servicio : servicios.values()) {
            if (Objects.equals(servicio.getNombreServicio(), solicitudBroker.getServicioSolicitado())){
                return servicio;
            }
        }
        throw new ServicioNoEncontrado("El servicio no esta registrado");
    }

    private Respuesta invocar(DatosServicio servicio, SolicitudBrokerPedidos solicitudBroker){
        try(
                Socket socket = new Socket(servicio.getHost(), servicio.getPuerto());
                OutputStreamWriter salida = new OutputStreamWriter(
                        socket.getOutputStream(), StandardCharsets.UTF_8
                );
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
                );
        ){
            SolicitudServidor solicitudServidor;

            if(Objects.equals(servicio.getNombreServicio(), "consultarPedido")){
                solicitudServidor = SolicitudServidor.consultarPedido(solicitudBroker.getId());

            } else if (Objects.equals(servicio.getNombreServicio(), "registrarPedido")){
                solicitudServidor = SolicitudServidor.registrarPedido(solicitudBroker.getCliente());

                for(LinkedHashMap<String, Object> producto : solicitudServidor.getProductos()){
                    solicitudServidor.agregarProducto(
                            (String) producto.get("nombre"),
                            (double) producto.get("precio"),
                            (int) producto.get("cantidad"),
                            (int) producto.get("existencia")
                    );
                }
            } else{
                solicitudServidor = SolicitudServidor.listarPedidos();
            }
            String jsonSolicitud = solicitudJsonAdapter.serializar(solicitudServidor);
            salida.write(jsonSolicitud + "\n");
            salida.flush();

            String jsonRecibido = entrada.readLine();
            System.out.println(jsonRecibido);
            return solicitudJsonAdapter.deserializarRespuesta(jsonRecibido);


        } catch (Exception e) {
            return Respuesta.respuestaERROR(
                    servicio.getNombreServicio(),
                    "Ocurrio un error al contactar al servicio" + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        new Broker(9000).iniciar();
    }
}
