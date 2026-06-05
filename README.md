# 💳 Wallet API — Spring Boot

API REST de billetera digital desarrollada con **Spring Boot 3**, diseñada como proyecto de aprendizaje personal con enfoque en tecnologías modernas de desarrollo backend. El proyecto integra **Openpay.mx** como pasarela de pagos y utiliza **Docker** para el despliegue de la aplicación en **Render**.

---

# 🚀 Despliegue en Producción
La aplicación se encuentra desplegada y operativa en el siguiente enlace:
🔗 **[Probar API en Render](https://wallet-api-prod.onrender.com)**

*Nota: Debido a las políticas de suspensión automática del plan gratuito de Render, si el servidor no ha recibido peticiones en los últimos 15 minutos, puede demorar entre 40 y 50 segundos en responder a la primera petición mientras el contenedor Docker se reactiva.*

---

## 🚀 Tecnologías utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.x | Framework base |
| Spring Security | 6 | Seguridad y autenticación |
| Spring Data JPA | — | Persistencia de datos |
| PostgreSQL | 15 | Base de datos relacional |
| Flyway | — | Migraciones de base de datos |
| JWT (jjwt) | 0.13.0 | Tokens de autenticación |
| MapStruct | 1.5.5 | Mapeo de entidades y DTOs |
| Lombok | 1.18.30 | Reducción de boilerplate |
| WebFlux | — | Cliente HTTP reactivo |
| Openpay MX SDK | 1.7.0 | Integración pasarela de pagos |
| Docker | — | Containerización |
| Render | — | Plataforma de despliegue |

---

## 🎯 Objetivo del proyecto

Este proyecto fue creado con fines de **aprendizaje**. Los principales objetivos fueron:

- Integrar una **pasarela de pagos real** (Openpay.mx) para procesar cobros con tarjeta.
- Implementar autenticación stateless con **Spring Security 6 + JWT**.
- Contenerizar la aplicación con **Docker** y hacer un deploy real en **Render**.
- Practicar patrones como control de acceso por roles (`ADMIN` / `USER`), paginación de resultados y uso de Java Records como DTOs.

---

## ⚙️ Variables de entorno requeridas

Crea un archivo `.env` en la raíz del proyecto con las siguientes variables:

```env
DB_NAME=wallet_db
DB_USER=tu_usuario
DB_PASSWORD=tu_password
SECRETKEY=una_clave_secreta_larga_para_firmar_jwt
```

Para la integración con Openpay, asegúrate de configurar también tu `merchant_id` y `private_key` en `application.properties` o como variables de entorno adicionales.

---

## 🐳 Levantar con Docker Compose

```bash
# Clonar el repositorio
git clone https://github.com/Jovas312/wallet.git
cd wallet

# Compilar el JAR
./mvnw clean package -DskipTests

# Levantar la app y la base de datos
docker-compose up --build
```

La API quedará disponible en: `http://localhost:8080`

---

## 📡 Guía de uso de la API

> **Base URL LOCAL-HOST:** `http://localhost:8080/api/v1`
> 
> **Base URL Render:** `https://wallet-api-prod.onrender.com/api/v1`
>
> Los endpoints marcados con 🔒 requieren el header `Authorization: Bearer <token>`.

---

### 🔐 Autenticación — `/auth`

#### `POST /api/v1/auth/register`
Registra un nuevo usuario y devuelve un JWT.

**Body:**
```json
{
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "password": "miPassword123",
  "documentId": "PERJ900101ABC"
}
```

**Respuesta `201 Created`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "juan@email.com",
  "role": "USER"
}
```

| Campo | Tipo | Requerido | Descripción |
|---|---|---|---|
| `firstName` | String | ✅ | Nombre del usuario |
| `lastName` | String | ✅ | Apellido del usuario |
| `email` | String | ✅ | Correo electrónico válido |
| `password` | String | ✅ | Mínimo 8 caracteres |
| `documentId` | String | ✅ | INE, RFC, pasaporte, etc. |

---

#### `POST /api/v1/auth/login`
Inicia sesión y obtiene un JWT.

**Body:**
```json
{
  "email": "juan@email.com",
  "password": "miPassword123"
}
```

**Respuesta `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "juan@email.com",
  "role": "USER"
}
```

---

### 👤 Usuarios — `/users`

#### `GET /api/v1/users/{email}` 🔒
Obtiene los datos de un usuario por su email.

**Respuesta `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@email.com",
  "role": "USER",
  "documentId": "PERJ900101ABC",
  "createdAt": "2024-01-15T10:30:00"
}
```

---

#### `PUT /api/v1/users/update/{email}` 🔒
Actualiza los datos de un usuario. Solo puede hacerlo el propio usuario o un `ADMIN`.

**Body:** Misma estructura que el registro.

---

#### `DELETE /api/v1/users/deleted/{email}` 🔒
Elimina un usuario. Solo el propio usuario o un `ADMIN`.

**Respuesta:** `204 No Content`

---

### 💰 Transacciones — `/transactions`

#### `POST /api/v1/transactions/deposit` 🔒
Deposita saldo a la wallet del usuario autenticado.

**Body:**
```json
{
  "amount": 500.00
}
```

> El monto mínimo es **$10.00**. El valor debe ser positivo.

**Respuesta `200 OK`:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "userId": "661e8400-e29b-41d4-a716-446655440111",
  "userEmail": "juan@email.com",
  "balance": 1500.00
}
```

---

#### `POST /api/v1/transactions/transfer` 🔒
Realiza una transferencia entre wallets.

**Body:**
```json
{
  "destinationEmail": "maria@email.com",
  "amount": 200.00
}
```

**Respuesta `200 OK`:**
```json
{
  "id": "abc12345-...",
  "type": "TRANSFER",
  "amount": 200.00,
  "sourceUserEmail": "juan@email.com",
  "destinationUserEmail": "maria@email.com",
  "status": "COMPLETED",
  "createdAt": "2024-01-15T11:00:00"
}
```

---

#### `GET /api/v1/transactions/{email}` 🔒
Lista el historial de transacciones de un usuario con paginación. Solo el propio usuario o un `ADMIN`.

**Query params opcionales:**
- `page` — número de página (default: `0`)
- `size` — tamaño de página (default: `20`)

**Ejemplo:** `GET /api/v1/transactions/juan@email.com?page=0&size=10`

---

#### `GET /api/v1/transactions/{id}/{email}` 🔒
Obtiene el detalle de una transacción específica por su UUID. Solo el propietario o un `ADMIN`.

**Ejemplo:** `GET /api/v1/transactions/abc12345-.../juan@email.com`

---

#### `POST /api/v1/transactions/pagos/cargo-tarjeta` 🔒
Ejecuta un cobro con tarjeta a través de la pasarela **Openpay.mx**.

**Body:**
```json
{
  "method": "card",
  "amount": 350.00,
  "description": "Depósito a wallet",
  "source_id": "tok_test_xxxxxxxxxxxxxxxx",
  "device_session_id": "kR1MiQhz2otdIuUMBpGA...",
  "customer": {
    "name": "Juan",
    "last_name": "Pérez",
    "email": "juan@email.com"
  }
}
```

> El `source_id` es el token de tarjeta generado por el JS de Openpay en el frontend. El `device_session_id` también lo genera Openpay en el cliente.

**Respuesta `200 OK`:**
```json
{
  "id": "trehzo5svbkspdxoiqm6",
  "amount": 350.00,
  "authorization": "801585",
  "status": "completed",
  "creation_date": "2024-01-15T11:05:00-06:00"
}
```

---

## 🔑 Roles y permisos

| Rol | Permisos |
|---|---|
| `USER` | Ver y actualizar su propio perfil, realizar depósitos, transfers y ver sus propias transacciones |
| `ADMIN` | Acceso total a todos los recursos de cualquier usuario |

---

## 📁 Estructura del proyecto

```
src/main/java/com/wallet/
├── controller/       # Controladores REST
├── dto/
│   ├── request/      # DTOs de entrada
│   └── response/     # DTOs de salida
├── entity/           # Entidades JPA
│   └── enums/        # Role, Status, Type
├── repository/       # Repositorios Spring Data
├── service/          # Lógica de negocio
│   └── impl/         # Implementaciones + ExternalApiGateway
└── security/         # Configuración JWT y Spring Security
```

---

## 📝 Notas

- Las migraciones de base de datos son gestionadas automáticamente por **Flyway** al iniciar la aplicación.
- Al registrarse, se crea automáticamente una **Wallet** asociada al usuario con saldo `0.00`.
- El deploy en **Render** se realizó conectando la imagen de Docker y configurando las variables de entorno directamente en el panel de Render, usando el `Dockerfile` incluido en el proyecto.
