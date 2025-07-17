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

**🔥 IMPORTANTE**: Tienes 2 opciones para crear la base de datos:

**📋 OPCIÓN 1: Creación Manual (Recomendado)**
```sql
-- Ejecutar en MySQL Workbench o cliente de línea de comandos
mysql -u root -p
source src/main/resources/database/schema.sql
```

**🚀 OPCIÓN 2: Creación Automática (Nuevo)**
La aplicación ahora puede crear la base de datos automáticamente al ejecutarse por primera vez:
- Solo necesitas tener MySQL Server instalado y configurado
- La aplicación detectará si la BD no existe y la creará automáticamente
- Creará las tablas básicas y datos iniciales

```bash
# Solo ejecutar la aplicación, creará todo automáticamente
mvn exec:java -Dexec.mainClass="com.inventario.vista.MenuPrincipal"
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

**Solución**: 
- **Automática**: La aplicación ahora detecta esto y crea la BD automáticamente
- **Manual**: Ejecutar el script `schema.sql` para crear la base de datos

#### Error de Permisos MySQL

```
❌ Access denied for user 'root'@'localhost' (using password: YES)
```

**Solución**: 
1. Verificar que MySQL Server esté ejecutándose
2. Verificar usuario y contraseña en `ConexionBD.java`
3. Probar conexión manual: `mysql -u root -p`

#### Error de Conexión al Servidor

```
❌ Communications link failure
```

**Solución**: 
1. Verificar que MySQL Server esté ejecutándose
2. Verificar el puerto (por defecto 3306)
3. Verificar firewall/antivirus

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
