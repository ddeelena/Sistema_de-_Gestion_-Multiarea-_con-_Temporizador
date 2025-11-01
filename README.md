# Sistema_de-_Gestion_-Multiarea-_con-_Temporizador
sistema para  Recibir órdenes de trabajo, asignarlas a una o varias áreas (1..n), monitorear estados y tiempos en segundos, y cerrarlas con trazabilidad completa.

# mvp-enrutador-ordenes

## 🚀 Características principales

- Gestión de **órdenes** y **áreas asignadas**.
- Control automático de estados según el tiempo (SLA).
- Estados soportados: `PENDIENTE`, `EN_PROGRESO`, `PAUSADA`, `COMPLETADA`, `TIMEOUT`.
- Servicio `TickService` que ejecuta actualizaciones cada minuto.
- Registro de cambios mediante la entidad **Historial**.
- Cálculo automático del **estado global** de cada orden.
- Arquitectura modular basada en repositorios, servicios y controladores REST.
- Pruebas unitarias con **JUnit 5** y **Mockito**.

Repositorio base para el MVP "Enrutador de Órdenes Multiárea con Temporizador".


## Tecnologías utilizadas 
- Java 21
- Spring Boot 3.5.7
- Spring Data MongoDB
- Lombok
- JUnit 5 + Mockito
- Maven


## Pasos para ejecutar 

Ejecución del proyecto

1. Clonar el repositorio:

git clone https://github.com/tuusuario/SistemaGestionMultitarea.git
cd SistemaGestionMultitarea


2. Compilar el proyecto:

mvn clean install


3. Ejecutar la aplicación:

mvn spring-boot:run


4. Acceder a la API:

http://localhost:8080/api/ordenes


Para ejecutar las pruebas

Ejecutar pruebas:

mvn test

## Metodos principales 


| Método | Endpoint               | Descripción                    |
| ------ | ---------------------- | ------------------------------ |
| `GET`  | `/api/ordenes`         | Lista todas las órdenes        |
| `POST` | `/api/ordenes`         | Crea una nueva orden           |
| `PUT`  | `/api/ordenes/{id}`    | Actualiza una orden existente  |
| `GET`  | `/api/areas/{ordenId}` | Obtiene las áreas de una orden |
| `POST` | `/api/areas/asignar`   | Asigna un área a una orden     |



