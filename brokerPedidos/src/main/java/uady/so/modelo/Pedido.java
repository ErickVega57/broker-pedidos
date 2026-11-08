package uady.so.modelo;

import java.util.List;

public class Pedido {
    private final String cliente;
    List<Producto> listaDeProductos;
    double subtotal;
    boolean posibleFraude;
    double descuento;
    double impuestos;
    double total;
    EstadoPedido estado;
    private int id;

    public Pedido(String cliente, List<Producto> listaDeProductos) {
        this.cliente = cliente;
        this.listaDeProductos = listaDeProductos;
        this.id = 0;
        this.subtotal = 0.0;
        this.posibleFraude = false;
        this.descuento = 0.0;
        this.impuestos = 0.0;
        this.total = 0.0;
        this.estado = EstadoPedido.NO_PROCESADO;
    }

    public String getCliente() {
        return cliente;
    }

    public List<Producto> getListaDeProductos() {
        return listaDeProductos;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setPosibleFraude(boolean posibleFraude) {
        this.posibleFraude = posibleFraude;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getImpuestos() {
        return impuestos;
    }

    public void setImpuestos(double impuestos) {
        this.impuestos = impuestos;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public int getId(){
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Pedido copy(){
        return new Pedido(this);
    }

    private Pedido(Pedido p){
        this.cliente = p.cliente;
        this.listaDeProductos = p.listaDeProductos;
        this.id = p.id;
        this.subtotal = p.subtotal;
        this.posibleFraude = p.posibleFraude;
        this.descuento = p.descuento;
        this.impuestos = p.impuestos;
        this.total = p.total;
        this.estado = p.estado;
    }
}
