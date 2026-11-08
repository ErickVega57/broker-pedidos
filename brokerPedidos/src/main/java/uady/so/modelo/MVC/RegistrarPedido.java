package uady.so.modelo.MVC;

import uady.so.datos.PedidoRepository;
import uady.so.modelo.EstadoPedido;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;

public class RegistrarPedido {

    private final PedidoRepository repositorio;   // ← nueva dependencia

    public RegistrarPedido(PedidoRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void registrarPedido(Pedido pedido) {
        // Validaciones
        if (pedido.getCliente() == null || pedido.getCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("El cliente no puede estar vacío.");
        }
        if (pedido.getListaDeProductos() == null || pedido.getListaDeProductos().isEmpty()) {
            throw new IllegalArgumentException("Debe existir al menos un producto.");
        }

        double subtotalCalculado = 0.0;
        for (Producto p : pedido.getListaDeProductos()) {
            if (p.getCantidadSolicitada() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero para: " + p.getNombreDeProducto());
            }
            if (p.getCantidadSolicitada() > p.getExistencia()) {
                throw new IllegalArgumentException("La cantidad supera la existencia para: " + p.getNombreDeProducto());
            }
            subtotalCalculado += (p.getPrecioDeProducto() * p.getCantidadSolicitada());
        }


        pedido.setSubtotal(subtotalCalculado);

        double descuentoCalculado = (subtotalCalculado >= 1000.0) ? (subtotalCalculado * 0.10) : 0.0;
        pedido.setDescuento(descuentoCalculado);

        double baseImponible = subtotalCalculado - descuentoCalculado;
        double impuestosCalculados = baseImponible * 0.16;
        pedido.setImpuestos(impuestosCalculados);

        pedido.setTotal(baseImponible + impuestosCalculados);
        pedido.setEstado(EstadoPedido.PROCESADO);

        // Almacenamiento
        int idAsignado = repositorio.guardar(pedido);
        pedido.setId(idAsignado);

        // Notificar a la Vista
        //this.data = pedido.copy();
        //notificar();

    }
}
