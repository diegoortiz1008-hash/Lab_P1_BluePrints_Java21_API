## Laboratorio #4 – REST API Blueprints (Java 21 / Spring Boot 3.3.x)
# Escuela Colombiana de Ingeniería – Arquitecturas de Software  
**Estudiante:** Diego Ortiz  
 
---
 
## 1. Descripción general
 
API REST para gestión de planos arquitectónicos (*blueprints*). Cada plano pertenece a un autor, tiene un nombre y una lista de puntos (coordenadas x, y). El proyecto sigue una arquitectura por capas: modelo, persistencia, servicios, filtros y controladores.
 
---
 
## 2. Requisitos previos
 
- Java 21+
- Maven 3.9+
- Docker Desktop
---
 
## 3. Ejecución del proyecto
 
### 3.1 Levantar la base de datos PostgreSQL
 
```bash
docker compose up -d
```
 
Esto levanta un contenedor PostgreSQL con:
- Base de datos: `blueprintsdb`
- Usuario: `blueprints`
- Contraseña: `blueprints123`
- Puerto: `5432`
### 3.2 Iniciar la aplicación
 
**Sin filtro (modo por defecto):**
```bash
mvn spring-boot:run
```
 
**Con filtro de redundancia:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=redundancy
```
 
**Con filtro de undersampling:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=undersampling
```
 
---
 
## 4. Pruebas de la API
 
### 4.1 Obtener todos los blueprints
```bash
curl -s http://localhost:8080/api/v1/blueprints
```
 
### 4.2 Crear un blueprint
```bash
curl -i -X POST http://localhost:8080/api/v1/blueprints \
  -H "Content-Type: application/json" \
  -d "{\"author\":\"john\",\"name\":\"house\",\"points\":[{\"x\":1,\"y\":1},{\"x\":2,\"y\":2}]}"
```
 
### 4.3 Obtener blueprints por autor
```bash
curl -s http://localhost:8080/api/v1/blueprints/john
```
 
### 4.4 Obtener un blueprint específico
```bash
curl -s http://localhost:8080/api/v1/blueprints/john/house
```
 
### 4.5 Agregar un punto a un blueprint
```bash
curl -i -X PUT http://localhost:8080/api/v1/blueprints/john/house/points \
  -H "Content-Type: application/json" \
  -d "{\"x\":3,\"y\":3}"
```
 
---
 
## 5. Formato de respuesta uniforme (`ApiResponse<T>`)
 
Todas las respuestas siguen el mismo formato:
 
```json
{
  "code": 200,
  "message": "execute ok",
  "data": { ... }
}
```
 
| Código | Situación |
|--------|-----------|
| 200 | Consulta exitosa |
| 201 | Blueprint creado |
| 202 | Punto agregado |
| 400 | Blueprint ya existe |
| 404 | Recurso no encontrado |
 
---
 
## 6. Persistencia en PostgreSQL
 
Las tablas son creadas automáticamente por Hibernate al iniciar la aplicación:
 
- `blueprints` — almacena autor, nombre e id
- `blueprint_points` — almacena los puntos de cada blueprint
 
---
 
## 7. Documentación Swagger / OpenAPI
 
Con la aplicación corriendo, acceder a:
 
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)
  <img width="1512" height="809" alt="image" src="https://github.com/user-attachments/assets/3ea35adb-269b-496c-86a7-38ec1158c681" />

 
---
 
## 8. Filtros de blueprints
 
Los filtros se aplican al consultar un blueprint específico (`GET /api/v1/blueprints/{author}/{name}`).
 
| Perfil | Filtro | Comportamiento |
|--------|--------|----------------|
| *(ninguno)* | `IdentityFilter` | Devuelve los puntos sin modificar |
| `redundancy` | `RedundancyFilter` | Elimina puntos consecutivos duplicados |
| `undersampling` | `UndersamplingFilter` | Conserva 1 de cada 2 puntos |
 
 
---
 
## 9. Pruebas
 
```bash
mvn test
```
 
**Resultado:**
```
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```
 
Las pruebas cubren:
- `RedundancyFilter`: eliminación de duplicados consecutivos, conservación de no consecutivos, lista vacía
- `UndersamplingFilter`: conservación de índices pares, caso con 2 o menos puntos
- `BlueprintsServices`: creación, consulta, manejo de `BlueprintNotFoundException`
 
---
 
## 10. Buenas prácticas aplicadas
 
- **Versionamiento de API** con prefijo `/api/v1/` para facilitar futuras versiones sin romper clientes existentes.
- **Respuesta uniforme** con `ApiResponse<T>` que estandariza el formato de todas las respuestas.
- **Códigos HTTP semánticos** que comunican el resultado de cada operación claramente.
- **Separación de responsabilidades** en capas (modelo, persistencia, servicios, controladores).
- **Inyección de dependencias** con Spring para desacoplar filtros y repositorios.
- **Perfiles de Spring** para activar comportamientos distintos sin cambiar el código.
