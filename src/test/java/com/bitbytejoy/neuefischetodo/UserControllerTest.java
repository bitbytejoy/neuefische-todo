package com.bitbytejoy.neuefischetodo;

import com.bitbytejoy.neuefischetodo.model.Login;
import com.bitbytejoy.neuefischetodo.model.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final Gson gson = new Gson();

    @Test
    public void whenRegisterLoginFlow_thenStatus200()
            throws Exception {

        // Register user

        User user = new User();
        user.setName("Tester");
        user.setEmail("tester@example.com");
        user.setPassword("super-secret");

        mvc.perform(MockMvcRequestBuilders.post("/users")
            .content(gson.toJson(user))
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.jsonPath("$.id").isString(),
            MockMvcResultMatchers.jsonPath("$.name").value(user.getName()),
            MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()),
            MockMvcResultMatchers.jsonPath("$.password").doesNotExist()
        );

        // Login user

        Login login = new Login();
        login.setEmail(user.getEmail());
        login.setPassword(user.getPassword());

        mvc.perform(
            MockMvcRequestBuilders.post("/login")
                .content(gson.toJson(login))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.jsonPath("$.jwt").isString()
        );
    }
}
