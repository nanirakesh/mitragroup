# 🔐 Mitra API Documentation

## Authentication

All API calls require Bearer token authentication.

### Get Token
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com",
    "name": "John Doe",
    "role": "USER"
  }
}
```

### Use Token
```bash
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## API Endpoints

### 🔐 Authentication

#### Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Validate Token
```bash
POST /api/auth/validate
Authorization: Bearer <token>
```

### 👤 User APIs

#### Get User Requests
```bash
GET /api/user/requests
Authorization: Bearer <token>
```

#### Create Service Request
```bash
POST /api/user/request
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Fix kitchen sink",
  "description": "Kitchen sink is leaking",
  "serviceType": "PLUMBING",
  "priority": "HIGH",
  "address": "123 Main St",
  "city": "Mumbai",
  "pincode": "400001"
}
```

### 👷 Worker APIs

#### Get Assigned Requests
```bash
GET /api/worker/requests
Authorization: Bearer <token>
```

#### Update Request Status
```bash
POST /api/worker/request/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

## Example Usage

### JavaScript/Fetch
```javascript
// Login
const loginResponse = await fetch('/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'user@example.com',
    password: 'password123'
  })
});

const { token } = await loginResponse.json();

// Use token for API calls
const requestsResponse = await fetch('/api/user/requests', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

const requests = await requestsResponse.json();
```

### cURL
```bash
# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'

# Use token
curl -X GET http://localhost:8080/api/user/requests \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

### Python/Requests
```python
import requests

# Login
login_data = {
    "email": "user@example.com",
    "password": "password123"
}

response = requests.post('http://localhost:8080/api/auth/login', json=login_data)
token = response.json()['token']

# Use token
headers = {'Authorization': f'Bearer {token}'}
requests_response = requests.get('http://localhost:8080/api/user/requests', headers=headers)
```

## Response Format

### Success Response
```json
{
  "success": true,
  "data": {...},
  "message": "Operation successful"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message",
  "code": "ERROR_CODE"
}
```

## Status Codes
- **200**: Success
- **400**: Bad Request
- **401**: Unauthorized (Invalid/Missing token)
- **403**: Forbidden (Insufficient permissions)
- **404**: Not Found
- **500**: Internal Server Error

## Token Expiration
- **Default**: 24 hours
- **Refresh**: Login again to get new token
- **Validation**: Use `/api/auth/validate` to check token validity