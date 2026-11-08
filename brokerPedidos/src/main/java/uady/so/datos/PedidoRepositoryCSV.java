package uady.so.datos;

import uady.so.excepciones.PedidoNoEncontrado;
import uady.so.modelo.EstadoPedido;
import uady.so.modelo.Pedido;
import uady.so.modelo.Producto;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepositoryCSV implements PedidoRepository {

    private final String RUTA_ARCHIVO = "pedidos_db.txt";

    @Override
    public int guardar(Pedido pedido) {
        List<Pedido> pedidos = obtenerTodos();

        pedidos.add(pedido);

        sobrescribirArchivo(pedidos);
        return pedidos.size();
    }

    @Override
    public Pedido consultar(int id) throws PedidoNoEncontrado {

        List<Pedido> pedidos = obtenerTodos();
        int indice = id - 1;

        if (indice >= 0 && indice < pedidos.size()){
            return pedidos.get(indice);
        }else{
            throw new PedidoNoEncontrado("No se ha encontrado el pedido");
        }
    }

    @Override
    public List<Pedido> obtenerTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) return pedidos;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                pedidos.add(deserializarPedido(linea));
            }
        }catch (IOException e){
            throw new RuntimeException("Error al leer el archivo");
        }
        return pedidos;
    }

    private void sobrescribirArchivo(List<Pedido> pedidos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            for (Pedido p : pedidos) {
                bw.write(serializarPedido(p));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir el archivo");
        }
    }

    private String serializarPedido(Pedido p) {
        StringBuilder sb = new StringBuilder();
        sb.append(p.getCliente()).append(";")
                .append(p.getSubtotal()).append(";")
                .append(p.getDescuento()).append(";")
                .append(p.getImpuestos()).append(";")
                .append(p.getTotal()).append(";")
                .append(p.getEstado().name()).append(";");

        for (Producto prod : p.getListaDeProductos()) {
            sb.append(prod.getNombreDeProducto()).append(",")
                    .append(prod.getPrecioDeProducto()).append(",")
                    .append(prod.getCantidadSolicitada()).append(",")
                    .append(prod.getExistencia()).append("|");
        }
        return sb.toString();
    }

    private Pedido deserializarPedido(String linea) {
        String[] partes = linea.split(";");
        List<Producto> productos = new ArrayList<>();

        if (partes.length > 6 && !partes[6].isEmpty()) {
            String[] arrayProductos = partes[6].split("\\|");
            for (String prodStr : arrayProductos) {
                String[] datosProd = prodStr.split(",");
                // Usando el constructor exacto de Producto
                Producto prod = new Producto(
                        datosProd[0], // nombre
                        Double.parseDouble(datosProd[1]), // precio
                        Integer.parseInt(datosProd[2]), // cantidad
                        Integer.parseInt(datosProd[3]) // existecia
                );
                productos.add(prod);
            }
        }

        // Usando el constructor exacto de Pedido
        Pedido p = new Pedido(partes[0], productos);
        p.setSubtotal(Double.parseDouble(partes[1]));
        p.setDescuento(Double.parseDouble(partes[2]));
        p.setImpuestos(Double.parseDouble(partes[3]));
        p.setTotal(Double.parseDouble(partes[4]));
        p.setEstado(EstadoPedido.valueOf(partes[5]));

        return p;
    }


}
