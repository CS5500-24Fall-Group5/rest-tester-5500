package main

import (
    "bytes"
    "encoding/json"
    "net/http"
    "net/http/httptest"
    "testing"
)

func TestGetAllUsers(t *testing.T) {
    resetUsers()
    req := httptest.NewRequest("GET", "/users", nil)
    w := httptest.NewRecorder()
    router := setupRouter()
    router.ServeHTTP(w, req)

    if w.Code != http.StatusOK {
        t.Fatalf("Expected status 200, got %d", w.Code)
    }

    var users []User
    if err := json.Unmarshal(w.Body.Bytes(), &users); err != nil {
        t.Fatalf("Expected an empty list, got an error: %v", err)
    }
    if len(users) != 0 {
        t.Fatalf("Expected empty list of users, got: %v", users)
    }
}

func TestAddUser(t *testing.T) {
    resetUsers()
    newUser := map[string]string{"name": "John Doe"}
    body, _ := json.Marshal(newUser)
    req := httptest.NewRequest("POST", "/users", bytes.NewBuffer(body))
    req.Header.Set("Content-Type", "application/json")
    w := httptest.NewRecorder()

    router := setupRouter()
    router.ServeHTTP(w, req)

    if w.Code != http.StatusCreated {
        t.Fatalf("Expected status 201, got %d", w.Code)
    }

    var user User
    if err := json.Unmarshal(w.Body.Bytes(), &user); err != nil {
        t.Fatalf("Failed to parse response: %v", err)
    }
    if user.Name != "John Doe" {
        t.Errorf("Expected user name 'John Doe', got %s", user.Name)
    }
    if user.HoursWorked != 0 {
        t.Errorf("Expected hoursWorked 0, got %f", user.HoursWorked)
    }
}

func TestGetUserByID(t *testing.T) {
    resetUsers()
    addTestUser("John Doe")

    req := httptest.NewRequest("GET", "/users/1", nil)
    w := httptest.NewRecorder()

    router := setupRouter()
    router.ServeHTTP(w, req)

    if w.Code != http.StatusOK {
        t.Fatalf("Expected status 200, got %d", w.Code)
    }

    var user User
    if err := json.Unmarshal(w.Body.Bytes(), &user); err != nil {
        t.Fatalf("Failed to parse response: %v", err)
    }
    if user.ID != 1 || user.Name != "John Doe" {
        t.Errorf("Expected user ID 1 with name 'John Doe', got ID %d and name %s", user.ID, user.Name)
    }
}

func TestDeleteUser(t *testing.T) {
    resetUsers()
    addTestUser("John Doe")

    req := httptest.NewRequest("DELETE", "/users/1", nil)
    w := httptest.NewRecorder()

    router := setupRouter()
    router.ServeHTTP(w, req)

    if w.Code != http.StatusOK {
        t.Fatalf("Expected status 200, got %d", w.Code)
    }

    var deletedUser User
    if err := json.Unmarshal(w.Body.Bytes(), &deletedUser); err != nil {
        t.Fatalf("Failed to parse response: %v", err)
    }
    if deletedUser.ID != 1 {
        t.Errorf("Expected deleted user ID 1, got %d", deletedUser.ID)
    }
}

func resetUsers() {
    users = []User{}
    nextID = 1
}

func addTestUser(name string) {
    user := User{ID: nextID, Name: name, HoursWorked: 0}
    users = append(users, user)
    nextID++
}
