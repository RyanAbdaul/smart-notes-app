# JWT Authentication Setup

This document describes the JWT authentication implementation for the Smart Notes application.

## Overview

JWT (JSON Web Token) authentication has been successfully integrated into the Smart Notes backend. Users must register and login to access note endpoints.

## Features Implemented

### 1. User Management
- User registration with email and password
- Email uniqueness validation
- Password encryption using BCrypt
- First user automatically gets ADMIN role
- Subsequent users get USER role

### 2. JWT Token Generation
- Tokens are generated upon successful login
- Configurable secret key and expiration time
- Token contains user email as the subject
- Uses HS256 signing algorithm

### 3. Security Configuration
- Stateless session management
- JWT-based authentication filter
- Protected note endpoints (requires authentication)
- Public authentication endpoints (`/api/auth/**`)
- Public Swagger/OpenAPI documentation endpoints

### 4. User-Note Association
- Each note is associated with the user who created it
- Users can only view, update, and delete their own notes
- Note count returns only the authenticated user's notes

## API Endpoints

### Authentication Endpoints (Public)

#### Register
```
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Note Endpoints (Protected)

All note endpoints require the JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

- `POST /api/notes` - Create a new note (associated with authenticated user)
- `GET /api/notes` - Get all notes for the authenticated user
- `GET /api/notes/{id}` - Get a specific note (only if owned by authenticated user)
- `PATCH /api/notes/{id}` - Update a note (only if owned by authenticated user)
- `DELETE /api/notes/{id}` - Delete a note (only if owned by authenticated user)
- `GET /api/notes/count` - Count notes for the authenticated user

## Configuration

### application.yaml

```yaml
spring:
  jwt:
    secret: ${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}
    expiration: ${JWT_EXPIRATION:86400000}  # 24 hours in milliseconds
```

### Environment Variables

You can override the JWT configuration using environment variables:

- `JWT_SECRET` - Base64 encoded secret key for JWT signing (default provided)
- `JWT_EXPIRATION` - Token expiration time in milliseconds (default: 86400000 = 24 hours)

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### User Authority Table
```sql
CREATE TABLE user_authority (
    user_id BIGINT NOT NULL,
    authority VARCHAR(255) NOT NULL,
    CONSTRAINT fk_user_authority_user FOREIGN KEY (user_id) 
        REFERENCES users(id) ON DELETE CASCADE
);
```

### Notes Table (Updated)
```sql
ALTER TABLE notes ADD COLUMN user_id BIGINT NOT NULL;
ALTER TABLE notes ADD CONSTRAINT fk_notes_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

## Implementation Files

### Entities
- `User.java` - User entity implementing UserDetails
- `Authority.java` - Embeddable authority/role entity
- `Note.java` - Updated with user association

### Repositories
- `UserRepository.java` - User data access

### Services
- `JwtService.java` / `JwtServiceImpl.java` - JWT token operations
- `AuthenticationService.java` / `AuthenticationServiceImpl.java` - Registration and login logic
- `NoteService.java` - Updated with user-based authorization

### Configuration
- `SecurityConfig.java` - Spring Security configuration
- `JwtAuthenticationFilter.java` - JWT token validation filter

### Controllers
- `AuthenticationController.java` - Registration and login endpoints
- `NoteController.java` - Protected note endpoints

### Request/Response DTOs
- `RegisterRequest.java` - Registration data
- `AuthenticationRequest.java` - Login credentials
- `AuthenticationResponse.java` - JWT token response

## Security Features

1. **Password Encryption**: All passwords are encrypted using BCrypt
2. **Token Validation**: Every request validates JWT token signature and expiration
3. **Authorization**: Users can only access their own notes
4. **Stateless Sessions**: No server-side session storage
5. **CSRF Protection**: Disabled (not needed for stateless JWT authentication)

## Testing the API

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

### 3. Create a Note (with token)
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "title": "My First Note",
    "description": "This is a test note"
  }'
```

### 4. Get All Notes
```bash
curl -X GET http://localhost:8080/api/notes \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Dependencies Added

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT Dependencies (already present) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

## Migration Path

The database migration `V3__create_users_and_auth_tables.sql` will automatically:
1. Create the users and user_authority tables
2. Add user_id column to the notes table
3. Create necessary indexes and foreign key constraints

**Note**: Existing notes in the database will be associated with user_id = 1 by default. Make sure to register the first user before accessing existing notes.

## Next Steps

- Implement refresh token mechanism
- Add password reset functionality
- Implement role-based access control for admin features
- Add rate limiting for authentication endpoints
- Implement account email verification
