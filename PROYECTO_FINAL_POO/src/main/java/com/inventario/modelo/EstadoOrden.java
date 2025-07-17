package com.inventario.modelo;

public enum EstadoOrden {
    PENDIENTE("Pendiente"),
    ENVIADO("Enviado"),
    APROBADA("Aprobada"),
    RECIBIDA("Recibida"),
    PARCIAL("Parcial"),
    CANCELADA("Cancelada"),
    ENTREGADA("Entregada"),
    FACTURADA("Facturada");

    private final String descripcion;

    EstadoOrden(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
