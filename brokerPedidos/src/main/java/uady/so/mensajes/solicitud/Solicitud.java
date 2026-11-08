package uady.so.mensajes.solicitud;

import java.util.LinkedHashMap;

// clase general de la estructura de una solicitud.
public class Solicitud {
    // hasmap raiz
    private final LinkedHashMap<Object,Object> data;
    // hasmap parametros
    private final LinkedHashMap<Object,Object> params;

    
    protected Solicitud(String nombreServicio) {
        this.data = new LinkedHashMap<>();
        this.params = new LinkedHashMap<>();
        data.put("servicio", nombreServicio);
        data.put("parametros", params);
    }

    protected void agregarParametro(String clave, Object valor){
        params.put(clave, valor);
    }

    // obtener el hashmap completo para la serializacion
    public LinkedHashMap<Object, Object> getData(){
        return (LinkedHashMap<Object, Object>)data;
    }

    protected Object getParametro(String clave){
        return params.get(clave);
    }
    
    public String getSolicitudTipo(){
        // "ejectuar", "listar", "registrar" ...
        return (String) data.get("servicio");
    }
}
