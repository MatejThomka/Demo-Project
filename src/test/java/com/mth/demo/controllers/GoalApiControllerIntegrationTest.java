package com.mth.demo.controllers;

import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mth.demo.models.dto.MessageDTO;
import com.mth.demo.models.entities.finance.Goal;
import com.mth.demo.models.entities.user.User;
import com.mth.demo.services.GoalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalApiControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GoalService goalService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void testCreateNewGoal() throws Exception {
    //Mock the behavior of the goalService.createNewGoal() method
    String authorization = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwdW1iYUBnbWFpbC5jb20iLCJpYXQiOjE2OTE0MzAxNDQsImV4cCI6MTY5MTQzMTU4NH0.2uvwlV1poCxyFG1YwV7e5xyYzBvMQt1VE4wKRraxPqU";
    User user = new User();
    Goal goal = new Goal("For a Baby", 16000.00, user); // Create a sample goal object with necessary fields

    // Mock the response returned by goalService.createNewGoal()
    MessageDTO expectedResponse = new MessageDTO("Your goal was created successfully!"); // Replace "Success" with the expected message

    when(goalService.createNewGoal(goal, authorization)).thenReturn(expectedResponse);

    // Perform the HTTP POST request to the "/api/goal/create" endpoint
    mockMvc.perform(MockMvcRequestBuilders
        .post("/api/goal/create")
        .header("Authorization", authorization) // Set the authorization header
        .contentType(MediaType.APPLICATION_JSON) // Convert goal object to JSON
        .content(objectMapper.writeValueAsString(goal)))
        .andExpect(MockMvcResultMatchers.status().isOk()); // Validate response
  }
}