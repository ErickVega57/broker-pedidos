package uady.so.mensajes.solicitud;

import java.util.LinkedHashMap;

public class Solicitud {
    private final LinkedHashMap<Object,Object> data;
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

    public LinkedHashMap<Object, Object> getData(){
        return (LinkedHashMap<Object, Object>)data;
    }

    protected Object getParametro(String clave){
        return params.get(clave);
    }
    public String getSolicitudTipo(){
        return (String) data.get("servicio");
    }
}
