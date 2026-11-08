package uady.so.json;

import com.google.gson.*;
import uady.so.mensajes.solicitud.SolicitudServidor;

import java.lang.reflect.Type;

public class SolicitudServidorDeserializador implements JsonDeserializer<SolicitudServidor> {

    @Override
    public SolicitudServidor deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        String servicio = jsonObject.get("servicio").getAsString();

        JsonObject parametros = jsonObject.has("parametros") && !jsonObject.get("parametros").isJsonNull()
                ? jsonObject.getAsJsonObject("parametros")
                : new JsonObject();

        switch (servicio) {
            case "registrarPedido":
                String cliente = parametros.get("cliente").getAsString();
                SolicitudServidor solicitudRegistro = SolicitudServidor.registrarPedido(cliente);

                if (parametros.has("productos") && parametros.get("productos").isJsonArray()) {
                    JsonArray productos = parametros.getAsJsonArray("productos");

                    for (JsonElement elemento : productos) {
                        JsonObject producto = elemento.getAsJsonObject();
                        solicitudRegistro.agregarProducto(
                                producto.get("nombre").getAsString(),
                                producto.get("precio").getAsDouble(),
                                producto.get("cantidad").getAsInt(),
                                producto.get("existencia").getAsInt()
                        );
                    }
                }
                return solicitudRegistro;

            case "consultarPedido":
                int id = parametros.get("id").getAsInt();
                return SolicitudServidor.consultarPedido(id);

            case "listarPedidos":
            default:
                return SolicitudServidor.listarPedidos();
        }
    }
}
