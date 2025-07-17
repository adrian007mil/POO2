# FormularioMostrarOrdenes - Documentación

## Descripción

El `FormularioMostrarOrdenes` es un formulario completo para visualizar, buscar, filtrar y gestionar las órdenes de compra del sistema de inventario.

## Características Principales

### 🔍 **Visualización de Órdenes**

- **Tabla principal**: Muestra todas las órdenes de compra con información completa
- **Columnas**: ID, Código, Proveedor, Fecha, Estado, Monto Total, Usuario, Observaciones
- **Ordenamiento**: Click en columnas para ordenar datos
- **Selección**: Una sola orden seleccionable a la vez

### 🔍 **Filtros y Búsqueda**

- **Búsqueda de texto**: Buscar por cualquier campo visible
- **Filtro por estado**: TODOS, PENDIENTE, ENVIADO, APROBADA, RECIBIDA, PARCIAL, CANCELADA
- **Filtrado en tiempo real**: Los resultados se actualizan automáticamente

### 📋 **Detalles de Orden**

- **Tabla de detalles**: Muestra productos de la orden seleccionada
- **Información detallada**: Producto, Código, Cantidad, Precio Unitario, Subtotal, Cantidad Recibida
- **Actualización automática**: Se actualiza al seleccionar una orden

### ⚙️ **Funcionalidades**

- **Actualizar**: Recargar datos desde la base de datos
- **Ver Detalles**: Ventana modal con información completa de la orden
- **Eliminar**: Eliminación lógica de órdenes (marca como inactiva)
- **Contador**: Muestra total de órdenes y valor total

## Estructura del Formulario

### Panel Superior - Controles

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│ Buscar: [________] Estado: [TODOS ▼] [Actualizar] [Ver Detalles] [Eliminar]     │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Panel Central - Tablas

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           Órdenes de Compra                                     │
├─────┬────────┬───────────┬──────────┬────────┬────────────┬─────────┬──────────┤
│ ID  │ Código │ Proveedor │ Fecha    │ Estado │ Monto      │ Usuario │ Observ.  │
├─────┼────────┼───────────┼──────────┼────────┼────────────┼─────────┼──────────┤
│ 1   │ ORD001 │ Proveedor │ 15/01/25 │ PENDIENTE │ S/ 2,500 │ Admin   │ Urgente  │
└─────┴────────┴───────────┴──────────┴────────┴────────────┴─────────┴──────────┘

┌─────────────────────────────────────────────────────────────────────────────────┐
│                      Detalles de la Orden Seleccionada                          │
├─────┬────────────────┬─────────┬──────────┬─────────────┬─────────────┬─────────┤
│ ID  │ Producto       │ Código  │ Cantidad │ Precio Unit │ Subtotal    │ Recibida│
├─────┼────────────────┼─────────┼──────────┼─────────────┼─────────────┼─────────┤
│ 1   │ Laptop Dell    │ P001    │ 2        │ S/ 1,250    │ S/ 2,500    │ 0       │
└─────┴────────────────┴─────────┴──────────┴─────────────┴─────────────┴─────────┘
```

### Panel Inferior - Información

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│ Total de órdenes: 25  |  Valor total: S/ 45,750.00                             │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Flujo de Uso

### 1. **Visualización General**

1. Al abrir el formulario, se cargan todas las órdenes activas
2. Se muestra el conteo total y valor total en la parte inferior
3. Se habilitan los controles de búsqueda y filtrado

### 2. **Búsqueda y Filtrado**

1. **Búsqueda por texto**: Escribir en el campo "Buscar" y presionar Enter
2. **Filtro por estado**: Seleccionar estado específico del dropdown
3. **Combinación**: Usar ambos filtros simultáneamente
4. **Limpiar**: Vaciar búsqueda y seleccionar "TODOS" para mostrar todo

### 3. **Selección de Orden**

1. Click en cualquier fila de la tabla de órdenes
2. Se cargan automáticamente los detalles en la tabla inferior
3. Se habilitan los botones "Ver Detalles" y "Eliminar"

### 4. **Ver Detalles**

1. Seleccionar una orden
2. Click en "Ver Detalles"
3. Se abre ventana modal con información completa:
   - Código de orden
   - Proveedor
   - Estado
   - Monto total
   - Usuario creador

### 5. **Eliminar Orden**

1. Seleccionar una orden
2. Click en "Eliminar"
3. Confirmar la eliminación en el diálogo
4. La orden se marca como inactiva (no se elimina físicamente)

## Integración con el Sistema

### Base de Datos

```sql
-- Consulta principal para órdenes
SELECT oe.*, p.Nombre as ProveedorNombre, p.RUC
FROM OrdenEntrada oe
INNER JOIN Proveedor p ON oe.ProveedorID = p.ID
WHERE oe.EsActivo = true
ORDER BY oe.FechaOrden DESC

-- Consulta para detalles de orden
SELECT doe.*, p.Nombre as ProductoNombre, p.CodigoProducto
FROM DetalleOrdenEntrada doe
INNER JOIN Producto p ON doe.ProductoID = p.ID
INNER JOIN OrdenEntrada oe ON doe.OrdenEntradaID = oe.ID
WHERE oe.CodigoOrden = ?
ORDER BY p.Nombre
```

### Clases Utilizadas

- **ConexionBD**: Acceso a base de datos
- **OrdenDeEntrada**: Modelo de orden
- **EstadoOrden**: Enum con estados de orden
- **Proveedor**: Modelo de proveedor

### Integración con MenuPrincipal

```java
// En MenuPrincipal.mostrarOpcionesInventario()
else if ("Mostrar Órdenes de Compra".equals(seleccion)) {
    new FormularioMostrarOrdenes().setVisible(true);
}
```

## Funcionalidades Técnicas

### Manejo de Errores

- **Conexión BD**: Captura errores de conexión y muestra mensajes informativos
- **Datos faltantes**: Validación de selección antes de operaciones
- **Eliminación**: Confirmación antes de eliminación lógica

### Rendimiento

- **Carga optimizada**: Solo carga órdenes activas
- **Filtrado eficiente**: Uso de RowFilter para filtrado en memoria
- **Actualización selectiva**: Solo recarga detalles cuando es necesario

### Interfaz de Usuario

- **Diseño responsivo**: Tablas con ancho ajustable
- **Feedback visual**: Botones deshabilitados cuando no hay selección
- **Colores consistentes**: Esquema de colores del sistema
- **Accesibilidad**: Tooltips y mensajes informativos

## Mejoras Futuras

### Funcionalidades Adicionales

1. **Exportar**: Generar reportes en PDF/Excel
2. **Imprimir**: Funcionalidad de impresión directa
3. **Editar**: Permitir modificación de órdenes pendientes
4. **Historial**: Mostrar cambios de estado de órdenes
5. **Notificaciones**: Alertas para órdenes vencidas

### Mejoras de UI/UX

1. **Paginación**: Para grandes volúmenes de datos
2. **Filtros avanzados**: Filtros por fecha, proveedor, monto
3. **Gráficos**: Visualización de estadísticas
4. **Temas**: Soporte para temas claro/oscuro

## Notas Técnicas

### Dependencias

- Java Swing para interfaz gráfica
- MySQL para base de datos
- JDBC para conectividad

### Rendimiento

- Recomendado para bases de datos con menos de 10,000 órdenes
- Para volúmenes mayores, implementar paginación

### Mantenimiento

- Verificar conexiones de BD regularmente
- Mantener índices en tablas OrdenEntrada y DetalleOrdenEntrada
- Monitorear rendimiento de consultas

## Conclusión

El `FormularioMostrarOrdenes` proporciona una interfaz completa y funcional para la gestión de órdenes de compra, integrándose perfectamente con el sistema de inventario existente y ofreciendo todas las funcionalidades necesarias para un manejo eficiente de las órdenes.
