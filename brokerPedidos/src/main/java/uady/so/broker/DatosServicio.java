package uady.so.broker;

public class DatosServicio {
    private final String nombreServicio;
    private final String host;
    private final int puerto;

    public DatosServicio(String nombreServicio, String host, int puerto) {
        this.nombreServicio = nombreServicio;
        this.host = host;
        this.puerto = puerto;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public String getHost() {
        return host;
    }

    public int getPuerto() {
        return puerto;
    }
}
