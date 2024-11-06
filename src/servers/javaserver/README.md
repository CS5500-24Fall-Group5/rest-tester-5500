# Java Server Setup (UserApiApplication)

## Prerequisites

- Java 8
- Maven

## Setup Instructions

### 1. Clone the Repository

Navigate to the project’s root directory and then into `src/main/java/com/rest`.

### 2. Install Dependencies

Use Maven to install all dependencies:

```sh
mvn clean install
```

### 3. Run the Server

Start the server using the following command:

```sh
mvn spring-boot:run
```

### 4. Server Details

- **Default Port**: 5003
  If you need to change the port, you can modify it in the `webServerFactoryCustomizer` method within the `UserApiApplication` class. However, it is recommended to keep it as 5003.

### API Endpoints

The server provides the following main API endpoints:

- **GET** `/users` - Fetch all users
- **GET** `/users/{userId}` - Fetch user by ID
- **POST** `/users` - Add a new userExample request body: `{"name": "Alice"}`
- **PUT** `/users/{userId}` - Update a user’s name by IDExample request body: `{"name": "NewName"}`
- **PATCH** `/users/{userId}` - Update hours worked for a userExample request body: `{"hoursToAdd": 5}`
- **DELETE** `/users` - Delete all users
- **DELETE** `/users/{userId}` - Delete a user by ID

#### Request and Response Format

All requests and responses are in JSON format.

### Testing

This application includes a set of JUnit test cases to validate the main functionalities. Ensure test dependencies are installed, then run the tests with the following command:

```sh
mvn test
```

#### Key Tests

The tests cover the following functionalities:

- Adding, updating, and deleting users
- Retrieving user lists and fetching users by ID
- Updating a user’s hours worked

### Additional Notes

- Ensure JDK and Maven are installed and configured before running the server.
- Cross-origin requests are enabled, allowing requests from any origin.
