
package proyecto_final_tp;


public class Producto {
    private String nombre;
    private double precio;
    private int cantidad;
    private String codigoProducto;
    private boolean esActivo;
    private static int contadorID = 1; // Para autoincremento
    
    // Constructor vacío
    public Producto() {
        this.codigoProducto = generarCodigoProducto();
        this.esActivo = true; // Por defecto siempre activo
    }
    
    // Constructor con parámetros (sin código ni activo porque se auto-generan)
    public Producto(String nombre, double precio, int cantidad) {
        this();
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }
    
    // Constructor completo
    public Producto(String nombre, double precio, int cantidad, String codigoProducto, boolean esActivo) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.codigoProducto = codigoProducto;
        this.esActivo = esActivo;
    }
    
    // Método para generar código automáticamente
    private String generarCodigoProducto() {
        return "PROD" + String.format("%04d", contadorID++);
    }
    
    // Getters
    public String getNombre() {
        return nombre;
    }
    
    public double getPrecio() {
        return precio;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public String getCodigoProducto() {
        return codigoProducto;
    }
    
    public boolean isEsActivo() {
        return esActivo;
    }
    
    // Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    
    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }
    
    public void setEsActivo(boolean esActivo) {
        this.esActivo = esActivo;
    }
    
    // Método para obtener el siguiente ID que se generará
    public static String obtenerSiguienteID() {
        return "PROD" + String.format("%04d", contadorID);
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "codigo='" + codigoProducto + '\'' +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", activo=" + esActivo +
                '}';
    }
}