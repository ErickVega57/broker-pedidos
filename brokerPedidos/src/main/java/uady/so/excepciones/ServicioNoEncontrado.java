package uady.so.excepciones;

public class ServicioNoEncontrado extends RuntimeException {
    public ServicioNoEncontrado(String message) {
        super(message);
    }
}
