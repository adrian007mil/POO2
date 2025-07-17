# Guía de Instalación y Configuración

## Sistema de Inventario WIN EMPRESAS v2.0

### Requisitos Previos

#### 1. Software Necesario

- **Java JDK 8** o superior
- **MySQL Server 8.0** o superior
- **Apache Maven 3.6** o superior
- **IDE** (NetBeans, IntelliJ IDEA, Eclipse)

#### 2. Configuración de MySQL

##### Instalación de MySQL

1. Descargar MySQL Server desde [https://dev.mysql.com/downloads/](https://dev.mysql.com/downloads/)
2. Instalar MySQL Server con las configuraciones por defecto
3. Configurar usuario `root` con contraseña

##### Crear la Base de Datos

```sql
-- Ejecutar en MySQL Workbench o cliente de línea de comandos
mysql -u root -p
source src/main/resources/database/schema.sql
```

### Configuración del Proyecto

#### 1. Clonar el Repositorio

```bash
git clone [URL_DEL_REPOSITORIO]
cd POO2/PROYECTO_FINAL_POO
```

#### 2. Configurar Base de Datos

1. Abrir el archivo `src/main/java/com/inventario/dao/ConexionBD.java`
2. Modificar las siguientes líneas:

```java
private static final String DB_PASSWORD = "tu_password_real";
```

#### 3. Instalar Dependencias

```bash
mvn clean install
```

#### 4. Ejecutar el Proyecto

```bash
mvn exec:java -Dexec.mainClass="com.inventario.vista.MenuPrincipal"
```

### Estructura de la Base de Datos

#### Tablas Principales

- **Producto**: Almacena información de productos
- **Proveedor**: Información de proveedores
- **TipoProducto**: Categorías de productos
- **OrdenEntrada**: Órdenes de compra
- **OrdenSalida**: Órdenes de venta
- **DetalleOrdenEntrada**: Detalles de compras
- **DetalleOrdenSalida**: Detalles de ventas

#### Campos Importantes

- **Indicador**: 'S' = Activo, 'N' = Inactivo
- **CodProducto**: Código único del producto
- **Stock**: Cantidad disponible
- **Precio**: Precio unitario

### Funcionalidades Principales

#### 1. Gestión de Productos

- ✅ Agregar productos
- ✅ Modificar productos
- ✅ Eliminar productos (lógicamente)
- ✅ Buscar productos
- ✅ Actualizar stock

#### 2. Gestión de Proveedores

- ✅ Agregar proveedores
- ✅ Modificar proveedores
- ✅ Eliminar proveedores
- ✅ Calificación de proveedores

#### 3. Gestión de Órdenes

- ✅ Órdenes de entrada (compras)
- ✅ Órdenes de salida (ventas)
- ✅ Seguimiento de estados
- ✅ Actualización automática de stock

#### 4. Reportes

- ✅ Inventario completo
- ✅ Productos con stock bajo
- ✅ Productos más vendidos
- ✅ Valor total del inventario
- ✅ Proveedores mejor calificados

### Solución de Problemas

#### Error de Conexión a MySQL

```
❌ Error de conexión: Access denied for user 'root'@'localhost'
```

**Solución**: Verificar usuario y contraseña en `ConexionBD.java`

#### Error de Driver No Encontrado

```
❌ Driver MySQL no encontrado
```

**Solución**: Ejecutar `mvn clean install` para descargar dependencias

#### Error de Base de Datos No Existe

```
❌ Unknown database 'BDVentas'
```

**Solución**: Ejecutar el script `schema.sql` para crear la base de datos

### Configuración Avanzada

#### Pool de Conexiones

Para mejorar el rendimiento en producción, considerar implementar un pool de conexiones como HikariCP:

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

#### Logging

El proyecto incluye SLF4J para logging. Los logs se muestran en consola por defecto.

### Seguridad

#### Recomendaciones

1. **Cambiar contraseñas por defecto**
2. **Usar conexiones SSL en producción**
3. **Implementar validación de entrada**
4. **Crear usuario específico para la aplicación**

#### Crear Usuario de Aplicación

```sql
CREATE USER 'inventario_user'@'localhost' IDENTIFIED BY 'password_seguro';
GRANT SELECT, INSERT, UPDATE, DELETE ON BDVentas.* TO 'inventario_user'@'localhost';
FLUSH PRIVILEGES;
```

### Mantenimiento

#### Respaldo de Base de Datos

```bash
mysqldump -u root -p BDVentas > backup_$(date +%Y%m%d).sql
```

#### Actualización de Dependencias

```bash
mvn versions:display-dependency-updates
```

### Contacto y Soporte

Para soporte técnico, contactar al equipo de desarrollo:

- **Empresa**: WIN EMPRESAS
- **Sistema**: Inventario v2.0
- **Fecha**: $(date +%Y-%m-%d)
