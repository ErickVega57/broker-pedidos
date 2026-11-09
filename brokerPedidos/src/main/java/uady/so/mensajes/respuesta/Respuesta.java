package uady.so.mensajes.respuesta;

import java.util.LinkedHashMap;

public class Respuesta {
    private final LinkedHashMap<Object, Object> data;
    private final LinkedHashMap<Object, Object> resultados;

    private final EstadoRespuesta estado;

    private Respuesta(String nombreServicio, EstadoRespuesta estado) {
        this.data = new LinkedHashMap<>();
        this.resultados = new LinkedHashMap<>();
        this.estado = estado;
        data.put("servicio", nombreServicio);
        data.put("estado", estado.toString());
    }

    public static Respuesta respuestaERROR(Respuesta respuestaServicio) {
        Respuesta respuesta = new Respuesta("ejecutar", EstadoRespuesta.ERROR);
        respuesta.agregarMensaje(respuestaServicio.getMensaje());
        return respuesta;
    }

    public LinkedHashMap<Object, Object> getData(){
        return data;
    }

    public static Respuesta respuestaOK(Respuesta respuestaServicio){
        Respuesta respuesta = new Respuesta("ejecutar", EstadoRespuesta.OK);
        respuesta.agregarResultados();
        respuesta.agregarResultado("servicio", respuestaServicio.getServicio());
        respuesta.agregarResultado("respuesta", respuestaServicio.getResultados());
        return respuesta;
    }

    public static Respuesta respuestaOK(String nombreServicio){
        Respuesta respuesta = new Respuesta(nombreServicio, EstadoRespuesta.OK);
        respuesta.agregarResultados();
        return respuesta;
    }

    public static Respuesta respuestaERROR(String nombreServicio, String mensaje){
        Respuesta respuesta = new Respuesta(nombreServicio, EstadoRespuesta.ERROR);
        respuesta.agregarMensaje(mensaje);
        return respuesta;
    }

    private void agregarMensaje(String mensaje){
        data.put("mensaje", mensaje);
    }

    private void agregarResultados(){
        data.put("resultado", resultados);
    }

    public void agregarResultado(String clave, Object valor){
        if(!data.containsKey("resultado")){
            throw new IllegalArgumentException();
        }
        resultados.put(clave, valor);
    }

    public void agregarComoResultado(Object valor){
        data.put("resultado", valor);
    }

    public String getServicio(){
        return (String) data.get("servicio");
    }

    public String getEstado(){
        return (String) data.get("estado");
    }

    public String getMensaje(){
        return (String) data.get("mensaje");
    }

    public Object getResultados(){
        return data.get("resultado");
    }
}
