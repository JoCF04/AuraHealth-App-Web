# 🔵 JOSE — Esqueleto + HU01 + HU02

## Tu rama
```
git checkout -b feature/HU01-HU02-auth-register
```

## ¿Qué implementaste?
| HU | Endpoint | Descripción |
|----|----------|-------------|
| HU01 | `POST /api/v1/users` | Registro de usuario (público) |
| HU02 | `POST /api/v1/auth/login` | Login + token JWT |

## ⚠️ Importante antes de subir
1. Cambia la contraseña de PostgreSQL en `src/main/resources/application.properties`
2. Crea la BD: `CREATE DATABASE "AuraHealthDB";`
3. Verifica que compila: `./mvnw clean package -DskipTests`
4. Swagger en: http://localhost:8080/swagger-ui.html

## Commits sugeridos
```bash
git add .
git commit -m "feat: skeleton base Spring Boot + seguridad JWT"
git commit -m "feat(HU01): POST /api/v1/users — registro de usuario"
git commit -m "feat(HU02): POST /api/v1/auth/login — autenticación JWT"
git push origin feature/HU01-HU02-auth-register
```
