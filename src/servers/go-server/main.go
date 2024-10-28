package main

import (
    "encoding/json"
    "log"
    "net/http"
    "strconv"
    "sync"

    "github.com/gorilla/handlers"
    "github.com/gorilla/mux"
)

type User struct {
    ID          int     `json:"id"`
    Name        string  `json:"name"`
    HoursWorked float64 `json:"hours_worked"`
}

var (
    users  []User
    nextID = 1
    mu     sync.Mutex
)

func main() {
    router := setupRouter()

    corsHandler := handlers.CORS(
        handlers.AllowedOrigins([]string{"*"}),
        handlers.AllowedMethods([]string{"GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"}),
        handlers.AllowedHeaders([]string{"Content-Type", "Authorization"}),
    )(router)

    log.Println("Server running on http://localhost:5004")
    log.Fatal(http.ListenAndServe(":5004", corsHandler))
}

func setupRouter() *mux.Router {
    router := mux.NewRouter()
    router.HandleFunc("/users", getUsers).Methods("GET")
    router.HandleFunc("/users", addUser).Methods("POST")
    router.HandleFunc("/users", deleteAllUsers).Methods("DELETE")
    router.HandleFunc("/users/{id}", getUserByID).Methods("GET")
    router.HandleFunc("/users/{id}", updateUser).Methods("PUT")
    router.HandleFunc("/users/{id}", updateUserHours).Methods("PATCH")
    router.HandleFunc("/users/{id}", deleteUser).Methods("DELETE")
    
    return router
}

func getUsers(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    json.NewEncoder(w).Encode(users)
}

func getUserByID(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    id, _ := strconv.Atoi(mux.Vars(r)["id"])
    for _, user := range users {
        if user.ID == id {
            json.NewEncoder(w).Encode(user)
            return
        }
    }
    http.Error(w, "User not found", http.StatusNotFound)
}

func addUser(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    var newUser User
    if err := json.NewDecoder(r.Body).Decode(&newUser); err != nil || newUser.Name == "" {
        http.Error(w, "Invalid input", http.StatusBadRequest)
        return
    }
    newUser.ID = nextID
    newUser.HoursWorked = 0
    nextID++
    users = append(users, newUser)
    w.WriteHeader(http.StatusCreated)
    json.NewEncoder(w).Encode(newUser)
}

func updateUser(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    id, _ := strconv.Atoi(mux.Vars(r)["id"])
    var updateData struct {
        Name *string `json:"name"`
    }
    if err := json.NewDecoder(r.Body).Decode(&updateData); err != nil {
        http.Error(w, "Invalid input", http.StatusBadRequest)
        return
    }
    for i, user := range users {
        if user.ID == id {
            if updateData.Name != nil && *updateData.Name != "" {
                users[i].Name = *updateData.Name
            }
            json.NewEncoder(w).Encode(users[i])
            return
        }
    }
    http.Error(w, "User not found", http.StatusNotFound)
}

func updateUserHours(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    id, _ := strconv.Atoi(mux.Vars(r)["id"])
    var updateData struct {
        HoursToAdd float64 `json:"hoursToAdd"`
    }

    if err := json.NewDecoder(r.Body).Decode(&updateData); err != nil {
        http.Error(w, "Invalid input", http.StatusBadRequest)
        return
    }
    for i, user := range users {
        if user.ID == id {
            users[i].HoursWorked += updateData.HoursToAdd
            json.NewEncoder(w).Encode(users[i])
            return
        }
    }
    http.Error(w, "User not found", http.StatusNotFound)
}

func deleteAllUsers(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    users = []User{}
    nextID = 1
    w.WriteHeader(http.StatusOK)
    json.NewEncoder(w).Encode(users)
}

func deleteUser(w http.ResponseWriter, r *http.Request) {
    mu.Lock()
    defer mu.Unlock()
    id, _ := strconv.Atoi(mux.Vars(r)["id"])
    for i, user := range users {
        if user.ID == id {
            users = append(users[:i], users[i+1:]...)
            json.NewEncoder(w).Encode(user)
            return
        }
    }
    http.Error(w, "User not found", http.StatusNotFound)
}
