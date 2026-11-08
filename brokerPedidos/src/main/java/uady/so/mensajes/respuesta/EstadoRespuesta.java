package uady.so.mensajes.respuesta;

public enum EstadoRespuesta {
    OK("OK"),
    ERROR("ERROR");

    private final String cadena;

    private EstadoRespuesta(String cadena) {
        this.cadena = cadena;
    }

    @Override
    public String toString() {
        return cadena;
    }
}
