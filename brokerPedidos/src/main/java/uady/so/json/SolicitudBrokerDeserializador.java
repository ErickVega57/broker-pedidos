package uady.so.json;

import com.google.gson.*;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudBrokerPedidos;

import java.lang.reflect.Type;
import java.util.Objects;

public class SolicitudBrokerDeserializador implements JsonDeserializer<SolicitudBroker> {

    @Override
    public SolicitudBroker deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        String servicioRaiz = jsonObject.get("servicio").getAsString();
        JsonObject parametros = jsonObject.getAsJsonObject("parametros");

        if (Objects.equals(servicioRaiz, "ejecutar")) {

            String servicioPedido = parametros.get("servicio").getAsString();
            JsonObject argumentos = parametros.getAsJsonObject("argumentos");

            if (Objects.equals(servicioPedido, "registrarPedido")) {
                // CORRECCIÓN: Usar getAsString() en lugar de toString()
                String cliente = argumentos.get("cliente").getAsString();
                JsonArray productos = argumentos.get("productos").getAsJsonArray();

                SolicitudBrokerPedidos solicitud = SolicitudBrokerPedidos.registarPedido(cliente);

                for (JsonElement object : productos) {
                    JsonObject producto = object.getAsJsonObject();
                    solicitud.agregarProducto(
                            producto.get("nombre").getAsString(),
                            producto.get("precio").getAsDouble(),
                            producto.get("cantidad").getAsInt(),
                            producto.get("existencia").getAsInt()
                    );
                }
                return solicitud; // Retornamos el Hijo

            } else if (Objects.equals(servicioPedido, "consultarPedido")) {
                // CORRECCIÓN: El id está dentro de argumentos, no en jsonObject
                int idPedidoBuscado = argumentos.get("id").getAsInt();
                return SolicitudBrokerPedidos.consultarPedido(idPedidoBuscado); // Retornamos el Hijo

            } else {
                return SolicitudBrokerPedidos.listarPedidos(); // Retornamos el Hijo
            }
        }

        // ---------------------------------------------------------
        // CASO 2: ES UNA SOLICITUD BROKER NORMAL (Clase Padre)
        // ---------------------------------------------------------
        else if (Objects.equals(servicioRaiz, "registrar")) {
            String nombreServicio = parametros.get("nombre").getAsString();
            String ipServicio = parametros.get("ip").getAsString();
            int puertoServicio = parametros.get("puerto").getAsInt();
            return SolicitudBroker.SolicitudRegistrar(nombreServicio, ipServicio, puertoServicio);

        } else { // Por descarte es "listar"
            if (parametros.get("buscar") == null) {
                return SolicitudBroker.SolicitudListarTodos();
            } else {
                String servicioBuscado = parametros.get("buscar").getAsString();
                return SolicitudBroker.SolicitudBuscar(servicioBuscado);
            }
        }
    }
}
