
# ARSW_Lab3

## Parte I

### Contexto
Este repositorio contiene una aplicación de ejemplo basada en Spring Boot que expone una API REST para la gestión de "blueprints" (planos). El proyecto está diseñado para ilustrar conceptos fundamentales del desarrollo de servicios web RESTful usando el framework Spring Boot en Java.

## Componentes y conectores - Solución

Este proyecto implementa la capa lógica de una aplicación para la gestión de planos arquitectónicos, usando Spring y un esquema de inyección de dependencias. Permite registrar, consultar y filtrar planos arquitectónicos de manera flexible y extensible.

### Componentes principales

- **BlueprintServices**: Servicio principal para la gestión de planos. Todas las operaciones de consulta pasan por aquí.
- **BlueprintsPersistence**: Interfaz de persistencia. Implementación por defecto: `InMemoryBlueprintPersistence`.
- **BlueprintFilter**: Interfaz para filtros de planos. Implementaciones:
	- `RedundancyBlueprintFilter`: Elimina puntos consecutivos repetidos.
	- `SubsamplingBlueprintFilter`: Elimina uno de cada dos puntos, de manera intercalada.

### Inyección de dependencias

La aplicación está configurada para que tanto el esquema de persistencia como el filtro de planos sean inyectados automáticamente mediante anotaciones de Spring (`@Autowired`, `@Qualifier`).  Para cambiar el filtro activo, basta con modificar el valor de `@Qualifier` en la inyección de `BlueprintFilter` en `BlueprintsServices`:

```java
@Autowired
@Qualifier("redundancy") // o "subsampling"
BlueprintFilter blueprintFilter;
```

### Funcionalidad implementada

- Registro de planos y consulta por nombre, autor o todos los planos.
- Persistencia en memoria (`InMemoryBlueprintPersistence`).
- Filtros configurables para reducir el tamaño de los planos antes de retornarlos.
- Extensible para agregar nuevos filtros o esquemas de persistencia.

### Pruebas

- **Unitarias**: Para cada filtro (`RedundancyBlueprintFilter`, `SubsamplingBlueprintFilter`), cubriendo casos normales y de borde.

- **Integración**: Verifican que el filtro inyectado se aplique correctamente al consultar planos desde los servicios.

## Cómo ejecutar el proyecto

### Prerrequisitos
- Java 8 o superior
- Maven 3.6 o superior

### Compilar el proyecto
```bash
mvn clean package
```

### Ejecutar la aplicación
```bash
mvn exec:java
```

### Ejecutar las pruebas
```bash
mvn test
```

## Ejemplo de uso y pruebas

### Uso de la aplicación
Al ejecutar la aplicación, se presenta un menú interactivo que permite:
1. Añadir un plano
2. Buscar un plano por nombre
3. Buscar planos por autor
4. Salir de la aplicación

Al consultar un plano, el resultado ya vendrá filtrado según el filtro activo, sin necesidad de modificar el resto de la aplicación. Puedes cambiar el filtro simplemente cambiando la anotación `@Qualifier` en la configuración de Spring.

### Pruebas disponibles
El proyecto incluye pruebas unitarias y de integración que se pueden ejecutar con:
```bash
mvn test
```
---
# Autor 
Juan Andres Rodriguez Peñuela


