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
        
        // objeto para asignar los ID aunque haya concurrencia.
        this.siguienteId = new AtomicInteger(1);
    }

    public void iniciar(){
        // entramos en escucha en el puerto
        // el broker crea un hilo para atender cada socket conectado
        
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
            // se lee la solicitud del socket conectado(cliente o servicio)
            String jsonSolicitud = entrada.readLine();
            System.out.println("Solicitud recibida");
            System.out.println(jsonSolicitud);

            // la solicitud se vuelve un objeto.
            SolicitudBroker solicitudBroker = solicitudJsonAdapter.deserializarSolicitudBroker(jsonSolicitud);

            // creamos una respuesta a la que le asignaremos valor segun el caso
            Respuesta respuesta;
            
            if(Objects.equals(solicitudBroker.getSolicitudTipo(), "registrar")){
                
                // si es de tipo registrar la enviamos a registrar servicio.
                respuesta = registrarServicio(solicitudBroker);
                
            } else if(Objects.equals(solicitudBroker.getSolicitudTipo(), "listar")){
                
                // si es de tipo listar la ejecutamos
                respuesta = listar(solicitudBroker);
                
            } else {
                // si no es ninguno es tipo "ejecutar"
                try {
                    
                    // buscamos el servicio solicitado para obtener los datos
                    // si no se encuentra envia excepcion ServicoNoEncontrado
                    DatosServicio servicio = buscarServicio((SolicitudBrokerPedidos)solicitudBroker);

                    
                    // invocamos el servicio y esperamos la respuesta.
                    Respuesta respuestaServicio = invocar(servicio, (SolicitudBrokerPedidos) solicitudBroker);

                    // creamos la respuesta para el cliente segun la respuesta del servicio
                    if(respuestaServicio.getEstado().equals("OK")){
                        respuesta = Respuesta.respuestaOK(respuestaServicio);
                    } else {
                        respuesta = Respuesta.respuestaERROR(respuestaServicio);
                    }

                }catch (ServicioNoEncontrado e){
                    // creamos respuesta de error
                    respuesta = Respuesta.respuestaERROR(
                            solicitudBroker.getSolicitudTipo(),
                            e.getMessage());
                }
            }
            // volvemos String formato Json la respuesta.
            String jsonRespuesta = solicitudJsonAdapter.serializar(respuesta);

            // enviamos al socket(cliente o broker) conectado la respuesta
            salida.write(jsonRespuesta + "\n");
            salida.flush();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // método para registrar servicios en el broker.
    private Respuesta registrarServicio(SolicitudBroker solicitudBroker){
        Integer id = siguienteId.getAndIncrement();
        // agregamos al hashmap (id, DatosServicio)
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

        // creamos la respuesta OK del servicio registrado
        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());
        
        // agregamos el id del servicio agregado
        respuesta.agregarResultado("id", id);

        return respuesta;
    }

    // metodo para las solicitudes tipo "listar"
    private Respuesta listar(SolicitudBroker solicitudBroker){
        
        if(solicitudBroker.getServicioBuscado() != null){
            
            // si servicio buscado no es nulo busca un servicio especifico
            return buscar(solicitudBroker);
        }
        // creamos respuesta
        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());

        // ciclos para aregar a cada respuesta
        // tipo [{"nombre" : "nombreServicio1},{"nombre" : "nombreServicio2"}]
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
        // creamos la respuesta OK.
        System.out.println("buscando servicio " + solicitudBroker.getServicioBuscado());

        Respuesta respuesta = Respuesta.respuestaOK(solicitudBroker.getSolicitudTipo());


        // ciclo para agregar los servicios que tengan coincidencia con el nombreBuscado.
        List<LinkedHashMap<String, String>> serviciosEncontrados = new ArrayList<>();
        for (DatosServicio servicio : servicios.values()) {
            LinkedHashMap<String, String> servicioMap = new LinkedHashMap<>();

            if(servicio.getNombreServicio().contains(solicitudBroker.getServicioBuscado())) {
                servicioMap.put("nombre", servicio.getNombreServicio());
                serviciosEncontrados.add(servicioMap);
            }
        }
        respuesta.agregarComoResultado(serviciosEncontrados);

        // si la lista queda vacia no existe el servicio.
        if(serviciosEncontrados.isEmpty()){
            respuesta = Respuesta.respuestaERROR(
                    solicitudBroker.getSolicitudTipo(),
                    "Servicio no registrado");
        }
        return respuesta;
    }

    
    private DatosServicio buscarServicio(SolicitudBrokerPedidos solicitudBroker){
        // buscamos el servicio en el hashmap devolvemos los datos.
        for (DatosServicio servicio : servicios.values()) {
            if (Objects.equals(servicio.getNombreServicio(), solicitudBroker.getServicioSolicitado())){
                return servicio;
            }
        }
        throw new ServicioNoEncontrado("El servicio no esta registrado");
    }

    // método para invocar a un servicio y obtener la respuesta.
    private Respuesta invocar(DatosServicio servicio, SolicitudBrokerPedidos solicitudBroker){
        // iniciamos la conexion con el servicio.

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

            // creamos la SolicitudServidor segun sea el caso del servicio solicitado.
            if(Objects.equals(servicio.getNombreServicio(), "consultarPedido")){
                solicitudServidor = SolicitudServidor.consultarPedido(solicitudBroker.getId());

            } else if (Objects.equals(servicio.getNombreServicio(), "registrarPedido")){

                solicitudServidor = SolicitudServidor.registrarPedido(solicitudBroker.getCliente());

                // agregamos los productos de la solicitud del broker a la del servicio
                for(LinkedHashMap<String, Object> producto : solicitudBroker.getProductos()){
                    solicitudServidor.agregarProducto(
                            (String) producto.get("nombre"),
                            (double) producto.get("precio"),
                            (int) producto.get("cantidad"),
                            (int) producto.get("existencia")
                    );
                }

            } else{
                // ultimo caso listar pedidos
                solicitudServidor = SolicitudServidor.listarPedidos();
            }
            // la solicitudServidor la volvemos String json y la enviamos.
            String jsonSolicitud = solicitudJsonAdapter.serializar(solicitudServidor);
            System.out.println("solicitud enviada al servicio: " + jsonSolicitud);
            salida.write(jsonSolicitud + "\n");
            salida.flush();

            // recibimos la respuesta
            String jsonRecibido = entrada.readLine();
            System.out.println("respuesta " + jsonRecibido);

            // devolvemos la respuesta como objeto.
            return solicitudJsonAdapter.deserializarRespuesta(jsonRecibido);

        } catch (Exception e) {
            // error al iniciar la conexion con el servicio.
            return Respuesta.respuestaERROR(
                    servicio.getNombreServicio(),
                    "Ocurrio un error al contactar al servicio" + e.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        // broker se inicia en escucha en el puerto 9000
        // por defecto
        int puertoBroker = 9000;

        if(args.length != 0){
            puertoBroker = Integer.parseInt(args[0]);
        }

        new Broker(puertoBroker).iniciar();
    }
}
