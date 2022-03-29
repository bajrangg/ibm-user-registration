package com.ibm;

import com.ibm.controller.UserRegistrationController;
import com.ibm.service.GeoLocationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
class UserRegistrationControllerTest {

    @Autowired
    private UserRegistrationController userRegistrationController;

    @MockBean
    GeoLocationService geoLocationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        assertThat(userRegistrationController).isNotNull();
    }

    @Test
    public void whenPostRequestToRegisterUserAndInvalidJson_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/register")
                        //.contentType(MediaType.APPLICATION_JSON)
                        .content("{}}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.status").isNotEmpty())
        ;
    }

    @Test
    public void whenPostRequestToRegisterUserAndInvalidUser_thenBadRequest() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors").isNotEmpty())
        ;
    }

    @Test
    public void whenPostRequestToRegisterUserAndValidUser_thenSuccess() throws Exception {

        Mockito.when(geoLocationService.getCityForIp("142.12.12.12")).thenReturn("Sydney");

        String user = "{\"userName\": \"testUser\", \"password\" : \"ABC123$%wew\", \"ipAddress\" : \"142.12.12.12\"}";

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.welcomeMessage").isNotEmpty());
    }

    @Test
    public void whenPostRequestToRegisterUserAndInvalidUserName_thenBadRequest() throws Exception {

        String user = "{\"userName\": \"\", \"password\" : \"ABC123$%wew\", \"ipAddress\" : \"142.12.12.12\"}";
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
                ;
    }

    @Test
    public void whenPostRequestToRegisterUserAndInvalidPassword_thenBadRequest() throws Exception {

        String user = "{\"userName\": \"testUser\", \"password\" : \"ABC123\", \"ipAddress\" : \"142.12.12.12\"}";
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors", hasSize(1)))
        ;
    }

}




