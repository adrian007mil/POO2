# Formulario de Orden de Entrada

## Descripción

Este formulario permite registrar órdenes de entrada al sistema de inventario. Una orden de entrada es una solicitud de productos a un proveedor específico.

## Características Principales

### 1. Información de la Orden

- **Código de Orden**: Se genera automáticamente al hacer clic en el campo
- **Proveedor**: Se selecciona de una lista de proveedores activos
- **Usuario Creador**: Por defecto "admin"
- **Estado**: Siempre "PENDIENTE" para nuevas órdenes
- **Fecha**: Se registra automáticamente la fecha actual
- **Monto Total**: Se calcula automáticamente

### 2. Gestión de Productos

- **Selección de Proveedor**: Al seleccionar un proveedor, se cargan automáticamente sus productos
- **Tabla de Productos del Proveedor**: Muestra todos los productos disponibles del proveedor seleccionado
- **Agregar Productos**: Para cada producto se puede especificar:
  - Cantidad requerida
  - Precio unitario
- **Lista de Items de la Orden**: Muestra los productos agregados a la orden actual
- **Remover Productos**: Se pueden eliminar productos de la orden

### 3. Funcionalidades

#### Agregar Producto a la Orden

1. Seleccionar un proveedor
2. Seleccionar un producto de la tabla "Productos del Proveedor"
3. Especificar la cantidad deseada
4. Ingresar el precio unitario
5. Hacer clic en "Agregar Producto"

#### Remover Producto de la Orden

1. Seleccionar un producto en la tabla "Items de la Orden"
2. Hacer clic en "Remover Producto"
3. Confirmar la eliminación

#### Calcular Total

- El total se calcula automáticamente al agregar/remover productos
- También se puede usar el botón "Calcular Total" para recalcular

#### Guardar Orden

1. Completar todos los campos obligatorios
2. Agregar al menos un producto
3. Hacer clic en "Guardar Orden"

## Validaciones

- El código de orden es requerido
- Se debe seleccionar un proveedor
- La orden debe tener al menos un producto
- La cantidad debe ser mayor a 0
- El precio unitario debe ser mayor a 0

## Uso del Formulario

### Paso a Paso para Crear una Orden

1. **Ejecutar la aplicación**:

   ```
   java com.inventario.vista.VentanaPrincipalOrdenEntrada
   ```

2. **Hacer clic en "Registrar Orden de Entrada"**

3. **Completar información básica**:

   - El código se genera automáticamente
   - Seleccionar un proveedor de la lista

4. **Agregar productos**:

   - Seleccionar un producto de la tabla izquierda
   - Ingresar cantidad y precio
   - Hacer clic en "Agregar Producto"
   - Repetir para todos los productos necesarios

5. **Revisar y guardar**:
   - Verificar los productos en la tabla derecha
   - Confirmar el monto total
   - Hacer clic en "Guardar Orden"

### Características Especiales

- **Auto-generación de código**: El código de orden se genera automáticamente con formato "OE-timestamp"
- **Validación de duplicados**: Si se agrega el mismo producto dos veces, se actualiza la cantidad
- **Cálculo automático**: El subtotal y total se calculan automáticamente
- **Interfaz intuitiva**: Tablas separadas para productos del proveedor y productos de la orden

## Estructura de Datos

### OrdenDeEntrada

- ID único (generado por BD)
- Código de orden
- Proveedor
- Fecha de orden
- Fecha de entrega
- Estado (PENDIENTE por defecto)
- Monto total
- Usuario creador
- Lista de ItemOrdenCompra

### ItemOrdenCompra

- Producto
- Cantidad
- Precio unitario
- Subtotal (calculado automáticamente)

## Notas Técnicas

- Se conecta a la base de datos MySQL
- Utiliza transacciones para garantizar consistencia
- Maneja errores de conexión y validación
- Interfaz responsive con diseño moderno

## Requisitos del Sistema

- Java 8 o superior
- MySQL con base de datos configurada
- Librerías de conexión MySQL (mysql-connector-java)

## Ejecución de Pruebas

Para probar el formulario directamente:

```java
java com.inventario.vista.FormularioOrdenEntrada
```

## Soporte

Para cualquier problema o consulta, revisar la documentación de la clase `ConexionBD` para configuración de base de datos.
