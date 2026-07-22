# AuraHealth — Plataforma de Salud Full-Stack

Plataforma web de gestión en salud desarrollada como proyecto académico para el curso de Desarrollo de Software de la UPC, bajo estándares de acreditación ABET/ICACIT. El sistema cubre el ciclo completo de gestión de pacientes y citas, cubriendo 30 historias de usuario distribuidas en seis épicas.


## Tecnologías utilizadas

- **Backend:** Java, Spring Boot, Spring Security (JWT)
- **Base de datos:** PostgreSQL
- **Documentación de API:** Swagger / OpenAPI
- **Contenedores:** Docker
- **Build:** Maven

## Funcionalidades principales

- Registro y autenticación de usuarios con JWT (HU01, HU02)
- Gestión de citas y recordatorios médicos
- API REST documentada con Swagger
- Despliegue en la nube (Render/Neon)

## Mi rol en el proyecto

Como líder técnico del equipo, mis responsabilidades principales fueron:

- Diseño del esquema relacional de 14 entidades en PostgreSQL.
- Definición de la arquitectura backend (Spring Boot) y la documentación de la API con Swagger/OpenAPI.
- Coordinación del equipo de desarrollo, asegurando trazabilidad de las 30 historias de usuario bajo estándares ABET/ICACIT.

## Cómo ejecutar el proyecto

```bash
git clone https://github.com/JoCF04/AuraHealth-App-Web.git
cd AuraHealth-App-Web
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080`, con la documentación Swagger en `http://localhost:8080/swagger-ui.html`.

## Equipo

Proyecto desarrollado por un equipo de 7 estudiantes de Ingeniería de Sistemas de Información (UPC). Ver [contribuidores](https://github.com/JoCF04/AuraHealth-App-Web/graphs/contributors).
