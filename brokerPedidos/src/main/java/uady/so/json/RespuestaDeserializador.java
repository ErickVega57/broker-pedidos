package uady.so.json;

import com.google.gson.*;
import uady.so.mensajes.respuesta.Respuesta;

import java.lang.reflect.Type;
import java.util.Map;

public class RespuestaDeserializador implements JsonDeserializer<Respuesta> {

    @Override
    public Respuesta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        JsonObject raiz = jsonObject.has("data") ? jsonObject.getAsJsonObject("data") : jsonObject;

        String servicio = raiz.get("servicio").getAsString();
        String estado = raiz.get("estado").getAsString();

        if (estado.equals("ERROR")) {
            String mensaje = raiz.has("mensaje") ? raiz.get("mensaje").getAsString() : "Error desconocido";
            return Respuesta.respuestaERROR(servicio, mensaje);
        }

        else {
            Respuesta respuesta = Respuesta.respuestaOK(servicio);

            if (raiz.has("resultado") && !raiz.get("resultado").isJsonNull()) {
                JsonElement resultadoElement = raiz.get("resultado");

                if (resultadoElement.isJsonObject()) {
                    // Si es un objeto (llave-valor), iteramos e insertamos con agregarResultado(clave, valor)
                    JsonObject resultadoObj = resultadoElement.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entrada : resultadoObj.entrySet()) {
                        // context.deserialize convierte automáticamente a Map, List, String, Double, etc.
                        Object valor = context.deserialize(entrada.getValue(), Object.class);
                        respuesta.agregarResultado(entrada.getKey(), valor);
                    }
                } else {
                    // Si es un Array lo agregamos directo
                    Object valor = context.deserialize(resultadoElement, Object.class);
                    respuesta.agregarComoResultado(valor);
                }
            }
            return respuesta;
        }
    }
}