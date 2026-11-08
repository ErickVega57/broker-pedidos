package uady.so.servidor.servicios;

import uady.so.mensajes.respuesta.Respuesta;
import uady.so.mensajes.solicitud.Solicitud;
import uady.so.mensajes.solicitud.SolicitudBroker;
import uady.so.mensajes.solicitud.SolicitudServidor;

public interface Servicio {
    Respuesta ejecutar(SolicitudServidor solicitud);
}
