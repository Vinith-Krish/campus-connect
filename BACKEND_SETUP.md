# Backend Setup Guide

## Prerequisites
- Java 17 or higher
- Maven or Gradle
- MySQL/PostgreSQL database

## Setup Instructions

### 1. Database Configuration
Create a database for the application:
```sql
CREATE DATABASE campus_connect;
```

### 2. Application Properties
Copy `application.properties.example` to `application.properties`:
```bash
cd campus-connect-backend/src/main/resources/
cp application.properties.example application.properties
```

### 3. Configure Your Properties
Edit `application.properties` with your actual values:
- Database credentials
- JWT secret key (generate a secure 256-bit key)
- Server port
- Other environment-specific settings

### 4. Generate JWT Secret Key
Use one of these methods:

**Option A: Online Generator**
- Visit: https://generate-random.org/encryption-key-generator
- Select 256-bit
- Copy the hex key

**Option B: Java Code**
```java
import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class JWTKeyGenerator {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256);
        String key = Base64.getEncoder().encodeToString(keyGen.generateKey().getEncoded());
        System.out.println(key);
    }
}
```

**Option C: OpenSSL**
```bash
openssl rand -base64 32
```

### 5. Run the Application
```bash
cd campus-connect-backend
mvn clean install
mvn spring-boot:run
```

## Important Notes
⚠️ **NEVER commit `application.properties` with real credentials!**
- The `.gitignore` file is configured to exclude it
- Only commit `application.properties.example` with placeholder values
- Each team member should create their own local `application.properties`
