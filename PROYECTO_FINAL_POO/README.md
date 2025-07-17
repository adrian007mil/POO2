# Sistema de Inventario WIN EMPRESAS v2.0

## Descripción

Sistema completo de gestión de inventario desarrollado en Java con interfaz gráfica Swing y base de datos MySQL. Permite la gestión integral de productos, proveedores, categorías y órdenes de compra.

## Características Principales

### 📦 Gestión de Inventario

- **Productos**: Registro, modificación y consulta con categorías
- **Proveedores**: Gestión completa con asociación de productos y precios
- **Categorías**: Organización jerárquica de productos
- **Stock**: Control automático de cantidades disponibles

### 🛒 Órdenes de Compra

- **Creación**: Formulario intuitivo con selección de proveedor
- **Estados**: PENDIENTE → ENVIADO → RECIBIDA (con actualización automática de stock)
- **Gestión**: Filtros por estado, envío a proveedor, recepción en almacén
- **Validación**: Controles de cantidad y precios

### 📊 Informes

- **Por Precio**: Órdenes ordenadas por mayor monto total
- **Por Cantidad**: Órdenes ordenadas por mayor cantidad de productos
- **Interfaz**: Tablas con rankings y resúmenes

## Requisitos del Sistema

### Software Necesario

- **Java JDK 8+**
- **MySQL Server 8.0+**
- **Apache Maven 3.6+**

### Instalación Rápida

1. **Configurar Base de Datos**

   ```sql
   mysql -u root -p
   source src/main/resources/database/schema_actualizado.sql
   ```

2. **Configurar Conexión**

   ```java
   // En ConexionBD.java
   private static final String DB_PASSWORD = "tu_password";
   ```

3. **Compilar y Ejecutar**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.inventario.vista.MenuPrincipal"
   ```

## Estructura del Proyecto

```
src/main/java/com/inventario/
├── modelo/          # Entidades del dominio
├── dao/             # Acceso a datos y conexión BD
├── vista/           # Interfaces gráficas (Swing)
└── ejemplo/         # Clases de prueba
```

## Funcionalidades Clave

### 🏪 Menú Principal

- **Registro**: Acceso a formularios de productos, proveedores y categorías
- **Órdenes**: Gestión completa del ciclo de órdenes de compra
- **Informes**: Reportes por precio y cantidad

### 📋 Formularios

- **Centrado automático**: Todas las ventanas aparecen centradas
- **Validación**: Controles de campos obligatorios y formatos
- **Navegación**: Interfaz intuitiva con botones de acción

### 🔄 Flujo de Órdenes

1. **Crear Orden**: Seleccionar proveedor → Agregar productos → Guardar
2. **Enviar**: Cambiar estado a ENVIADO
3. **Recibir**: Actualizar stock automáticamente → Estado RECIBIDA

## Uso Básico

### Crear Orden de Compra

1. Ejecutar aplicación → Menú "Órdenes" → "Registrar Orden"
2. Seleccionar proveedor (se cargan sus productos automáticamente)
3. Agregar productos con cantidad y precio
4. Guardar orden

### Gestionar Órdenes

1. Menú "Órdenes" → "Mostrar Órdenes"
2. Filtrar por estado si es necesario
3. Seleccionar orden → "Enviar a Proveedor" o "Recibir Orden"

### Ver Informes

1. Menú "Informes" → Seleccionar tipo de reporte
2. Consultar rankings y estadísticas

## Tecnologías Utilizadas

- **Java Swing**: Interfaz gráfica de usuario
- **MySQL**: Base de datos relacional
- **JDBC**: Conectividad con base de datos
- **Maven**: Gestión de dependencias

## Configuración de Desarrollo

### Base de Datos

- **Host**: localhost:3306
- **Usuario**: root
- **Base de Datos**: inventario_db

### Dependencias (Maven)

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

## Soporte

Para problemas de configuración, revisar:

- `ConexionBD.java` para configuración de base de datos
- `schema_actualizado.sql` para estructura de tablas
- Logs de consola para errores de conexión

---

**Desarrollado con Java Swing y MySQL** | **Interfaz gráfica completa** | **Control de inventario empresarial**
