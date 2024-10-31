# Go Server Setup

## Prerequisites
- Go 1.x installed
  - [**Download and install Go**](https://go.dev/doc/install)
- git (for downloading dependencies)

## Setup Instructions

### 1. Navigate to Project Directory
```sh
cd src/servers/go-server
```

### 2. Install Dependencies
The server requires the following packages:
```sh
go get github.com/gorilla/mux
go get github.com/gorilla/handlers
```

Alternatively, you can simply run:
```sh
go mod tidy
```

### 3. Run the Server
```sh
go run .
```
or
```sh
go run main.go
```

## API Endpoints

The server provides the following REST endpoints:

- `GET /users` - Get all users
- `POST /users` - Add a new user
- `DELETE /users` - Delete all users
- `GET /users/{id}` - Get user by ID
- `PUT /users/{id}` - Update user name
- `PATCH /users/{id}` - Update user hours
- `DELETE /users/{id}` - Delete a specific user

#### Additional Notes
- The server runs on port 5004 by default
- CORS is enabled for all origins
- Supported HTTP methods: GET, POST, PUT, PATCH, DELETE, OPTIONS
- All data is stored in memory and will be lost when the server restarts

## Data Structure

Users are stored with the following structure:
```go
type User struct {
    ID          int     `json:"id"`
    Name        string  `json:"name"`
    HoursWorked float64 `json:"hours_worked"`
}
```