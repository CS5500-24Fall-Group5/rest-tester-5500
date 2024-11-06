package com.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiApplicationTests {

  @Autowired
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() throws Exception {
    // Clear all users before each test
    mockMvc.perform(delete("/users"))
            .andExpect(status().isOk());
  }

  @Test
  public void testGetAllUsersInitiallyEmpty() throws Exception {
    mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }

  @Test
  public void testAddUser() throws Exception {
    String newUserJson = "{\"name\": \"Alice\"}";

    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Alice"))
            .andExpect(jsonPath("$.hoursWorked").value(0));
  }

  @Test
  public void testGetUserById() throws Exception {
    // Add a user first
    String newUserJson = "{\"name\": \"Bob\"}";
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated());

    // Fetch the user by ID
    mockMvc.perform(get("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Bob"));
  }

  @Test
  public void testGetUserByIdNotFound() throws Exception {
    mockMvc.perform(get("/users/99"))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));
  }

  @Test
  public void testDeleteAllUsers() throws Exception {
    String newUserJson = "{\"name\": \"Charlie\"}";
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated());

    // Delete all users
    mockMvc.perform(delete("/users"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));

    // Verify users list is empty
    mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
  }

  @Test
  public void testUpdateUser() throws Exception {
    String newUserJson = "{\"name\": \"David\"}";
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated());

    String updatedUserJson = "{\"name\": \"Daniel\"}";
    mockMvc.perform(put("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedUserJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Daniel"));
  }

  @Test
  public void testUpdateUserNotFound() throws Exception {
    String updatedUserJson = "{\"name\": \"NonExistent\"}";
    mockMvc.perform(put("/users/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(updatedUserJson))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));
  }

  @Test
  public void testUpdateUserHours() throws Exception {
    String newUserJson = "{\"name\": \"Eve\"}";
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated());

    String hoursUpdateJson = "{\"hoursToAdd\": 5}";
    mockMvc.perform(patch("/users/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(hoursUpdateJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hoursWorked").value(5));
  }

  @Test
  public void testDeleteUserById() throws Exception {
    String newUserJson = "{\"name\": \"Frank\"}";
    mockMvc.perform(post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(newUserJson))
            .andExpect(status().isCreated());

    mockMvc.perform(delete("/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Frank"));

    // Verify user is deleted
    mockMvc.perform(get("/users/1"))
            .andExpect(status().isNotFound());
  }
}
