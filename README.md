# BlueprintsRESTAPI

## 1. Descripción General
API REST para gestionar planos arquitectónicos (Blueprints). Provee operaciones para consultar todos los planos, consultar por autor, obtener un plano específico y crear/actualizar planos. Implementado con Spring Boot, Spring MVC, e inyección de dependencias basada en anotaciones.

## 2. Arquitectura / Componentes
- Capa API (REST Controller): `BlueprintApiController` expone `/blueprints`.
- Capa de Servicios: `BlueprintsServices` aplica filtros y orquesta persistencia.
- Capa de Persistencia: `InMemoryBlueprintPersistence` mantiene datos en memoria (estructura thread-safe).
- Filtros (Estrategia): Implementaciones de `BlueprintFilter` (Subsampling / Redundancy) aplicadas antes de retornar resultados.

Patrones aplicados: Inversión de Dependencias, Inyección de Dependencias (Spring), Strategy (filtros), Repository (persistencia), DTO implícito (modelo expuesto directamente).

## 3. Estructura de Código (carpetas relevantes)
```
src/main/java/edu/BlueprintsMain.java                   # Entry point Spring Boot
src/main/java/edu/eci/arsw/blueprints/controllers/      # REST controller
src/main/java/edu/eci/arsw/blueprints/services/         # Servicios de dominio
src/main/java/edu/eci/arsw/blueprints/persistence/      # Interfaces y excepciones
src/main/java/edu/eci/arsw/blueprints/persistence/impl/ # Implementación en memoria
src/main/java/edu/eci/arsw/blueprints/filter/           # Filtros de puntos
src/main/java/edu/eci/arsw/blueprints/model/            # Entidades Blueprint / Point
ANALISIS_CONCURRENCIA.txt                               # Revisión de concurrencia
```

## 4. Modelo de Datos Simplificado
`Blueprint`: author, name, List<Point>.
`Point`: x, y.

## 5. Endpoints REST
Base: `http://localhost:8080/blueprints`

| Método | Ruta                              | Descripción                                  | Respuesta Éxito |
|--------|-----------------------------------|----------------------------------------------|-----------------|
| GET    | /blueprints                       | Lista todos los planos (filtrados)           | 202 Accepted    |
| GET    | /blueprints/{author}              | Planos por autor                             | 202 o 404       |
| GET    | /blueprints/{author}/{bprintname} | Un plano específico                          | 202 o 404       |
| POST   | /blueprints                       | Crea un nuevo plano                          | 201 Created     |
| PUT    | /blueprints                       | (Actualmente reusa create; mejora pendiente) | 201 / 403       |

Notas:
- PUT idealmente debería dirigirse a `/blueprints/{author}/{bprintname}` para actualización idempotente (pendiente refactor).
- Códigos de error: 404 cuando no se encuentra plano/autor (persistencia lanza excepción). 403 al intentar crear duplicados.

## 6. Ejemplos curl
Listar todos:
```
curl -i http://localhost:8080/blueprints
```
Por autor:
```
curl -i http://localhost:8080/blueprints/Karol
```
Plano específico:
```
curl -i http://localhost:8080/blueprints/Karol/House
```
Crear:
```
curl -i -X POST -H "Content-Type: application/json" -d '{
  "author": "Ana",
  "points": [ {"x":10, "y":10}, {"x":15, "y":25} ]
  "name": "Studio",
}' http://localhost:8080/blueprints
```
Respuesta esperada: `HTTP/1.1 201 Created`.

Intento duplicado (mismo author+name) -> 403 Forbidden.

## 7. Filtros de Puntos
- `SubsamplingBlueprintFilter` (activo por @Qualifier): toma cada 2 puntos.
- `RedundancyBlueprintFilter`: elimina puntos consecutivos repetidos.
Se puede cambiar el filtro modificando el `@Qualifier` inyectado en `BlueprintsServices`.

## 8. Concurrencia
Ver en `ANALISIS_CONCURRENCIA.txt`.
Resumen rápido:
- Reemplazo de `HashMap` por `ConcurrentHashMap` para estructura thread-safe.
- Inserción atómica mediante `putIfAbsent` evita condición de carrera (check-then-act).
- Excepciones específicas distinguen autor/plano inexistente.
- Lecturas usan vistas weakly consistent; suficiente para este escenario.

## 9. Cómo Ejecutar
Compilar y correr:
```
mvn clean package
mvn spring-boot:run
```
Aplicación expone puerto 8080 (config por defecto de Spring Boot starter web si presente en pom).

## 10. Manejo de Errores
- 202 Accepted: peticiones GET exitosas.
- 201 Created: creación o (actual actualización actual) exitosa.
- 404 Not Found: recurso no existente.
- 403 Forbidden: intento de crear duplicado.
- 500 (si ocurriera): errores no controlados.

## 11. Posibles Mejoras Futuras
- Ajustar PUT a `/blueprints/{author}/{bpname}` con verificación de existencia y actualización idempotente.
- Hacer `Blueprint` inmutable (copias defensivas de lista de puntos).
- Validación de payload (Bean Validation @Valid + anotaciones).
- Manejo global de errores con `@ControllerAdvice` y JSON unificado.
- Tests de estrés concurrente (JMH o ThreadPool + assertions) para validar atomicidad.

## 12. Decisiones Clave
| Tema         | Decisión | Justificación |
|--------------|----------|---------------|
| Persistencia | ConcurrentHashMap | Atomicidad y paralelismo sin bloqueos globales |
| Filtro       | Strategy + Qualifier | Cambiar comportamiento sin tocar servicio |
| HTTP Codes   | 202 en GET | Requisito del laboratorio |
| Excepciones  | Personalizadas | Evitar uso de Exception genérica |

## 13. Verificación Rápida Manual
1. Levantar app.
2. GET /blueprints -> lista inicial (Karol, Juan, etc.).
3. POST nuevo plano -> 201.
4. GET plano recién creado -> 202.
5. POST repetido -> 403.
6. GET autor inexistente -> 404.

---
Documento preparado para revisión rápida. Cualquier duda adicional: revisar clases en paquetes indicados.
