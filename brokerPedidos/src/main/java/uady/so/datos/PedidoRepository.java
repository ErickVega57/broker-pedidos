package uady.so.datos;

import uady.so.excepciones.PedidoNoEncontrado;
import uady.so.modelo.Pedido;

import java.util.List;

public interface PedidoRepository {
    int guardar(Pedido pedido);
    Pedido consultar(int id) throws PedidoNoEncontrado;
    List<Pedido> obtenerTodos();
}
