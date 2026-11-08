package uady.so.mensajes.solicitud;

public class SolicitudBroker extends Solicitud {
    /*
     * Solicitudes de basicas al broker:
     *  - Listar
     *  - Registrar
     *  - Buscar
     */

    protected SolicitudBroker(String nombreServicio) {
        super(nombreServicio);
    }

    public static SolicitudBroker SolicitudRegistrar(String nombre, String ip, int puerto){
        SolicitudBroker solicitudBroker = new SolicitudBroker("registrar");
        solicitudBroker.agregarParametro("nombre", nombre);
        solicitudBroker.agregarParametro("ip",ip);
        solicitudBroker.agregarParametro("puerto",puerto);
        return solicitudBroker;
    }

    public static SolicitudBroker SolicitudListarTodos(){
        return new SolicitudBroker("listar");
    }

    public static SolicitudBroker SolicitudBuscar(String servicioBuscado){
        SolicitudBroker solicitudBroker = new SolicitudBroker("listar");
        solicitudBroker.agregarParametro("buscar",servicioBuscado);
        return solicitudBroker;
    }

    public static SolicitudBroker SolicitudEjecutar(String servicioObjetivo, Object argumentos){
        SolicitudBroker solicitud = new SolicitudBroker("ejecutar");
        solicitud.agregarParametro("servicio", servicioObjetivo);
        if(argumentos != null) {
            solicitud.agregarParametro("argumentos", argumentos);
        }
        return solicitud;
    }

    public String getServicioBuscado(){
        return (String) getParametro("buscar");
    }

    public String getIp(){
        return (String) getParametro("ip");
    }
    public String getNombreServicio(){
        return (String) getParametro("nombre");
    }
    public int getPuerto(){
        return (int) getParametro("puerto");
    }

}
