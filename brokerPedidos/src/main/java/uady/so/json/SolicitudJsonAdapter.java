package uady.so.json;

import com.google.gson.*;
import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.Solicitud;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudServidor;

import java.lang.reflect.Type;


/* Clase que Serializa y Deserializa solicitudes y respuestas
 * Convierte en formato json 
 *
 */
public class SolicitudJsonAdapter {

    private final Gson gson;

    public SolicitudJsonAdapter(){
        this.gson = new GsonBuilder()
                .registerTypeAdapter(SolicitudBroker.class, new SolicitudBrokerDeserializador())
                .registerTypeAdapter(SolicitudServidor.class, new SolicitudServidorDeserializador())
                .registerTypeAdapter(Respuesta.class, new RespuestaDeserializador())
                .create();
    }

    public String serializar(Solicitud solicitud){
        return gson.toJson(solicitud.getData());
    }

    public String serializar(Respuesta respuesta){

        // serializamos directamente la respuesta
        String json = gson.toJson(respuesta.getData());
        // volvemos el String un jsonObject.
        JsonObject jsonObject = JsonParser.parseString(json).getAsJsonObject();

        /* el siguiente bloque de codigo revisa si se tienen las claves
         * id, cantidad, existencia para volverlos enteros como se establece
         * en el protocolo de comunucacion.
         */ 
        if (jsonObject.has("resultado")) {
            JsonElement resultadoElement = jsonObject.get("resultado");

            if (resultadoElement.isJsonObject()) {
                JsonObject resultado = resultadoElement.getAsJsonObject();
                
                if (resultado.has("id")) {
                    resultado.addProperty("id", resultado.get("id").getAsInt());
                }
                if ((resultado.has("productos") && resultado.get("productos").isJsonArray()) ||
                        (
                                resultado.has("respuesta") &&
                                resultado.get("respuesta").isJsonObject() &&
                                resultado.get("respuesta").getAsJsonObject().has("productos")
                        )
                ) {
                    JsonArray productos;
                    if(resultado.has("productos") && resultado.get("productos").isJsonArray()){
                        productos = resultado.getAsJsonArray("productos");
                    } else{
                        JsonObject resp = resultado.getAsJsonObject("respuesta");
                        productos = resp.getAsJsonArray("productos");

                        if (resp.has("id")) {
                            resp.addProperty("id", resp.get("id").getAsInt());
                        }
                    }

                    // recorremos cada producto del arreglo
                    for (JsonElement element : productos) {
                        if (element.isJsonObject()) {
                            JsonObject producto = element.getAsJsonObject();

                            // sobreescribimos las propiedades que queremos forzar a enteros
                            if (producto.has("cantidad")) {
                                // Lee el número original (ej. 4.0) y lo guarda estrictamente como int (4)
                                producto.addProperty("cantidad", producto.get("cantidad").getAsInt());
                            }

                            if (producto.has("existencia")) {
                                producto.addProperty("existencia", producto.get("existencia").getAsInt());
                            }
                        }
                    }
                }
            }
        }
        return jsonObject.toString();
    }

    public SolicitudBroker deserializarSolicitudBroker(String json){
        return gson.fromJson(json, SolicitudBroker.class);
    }

    public SolicitudServidor deserializarSolicitudServidor(String json){
        return gson.fromJson(json, SolicitudServidor.class);
    }

    public Respuesta deserializarRespuesta(String json){
        return gson.fromJson(json, Respuesta.class);
    }

}
