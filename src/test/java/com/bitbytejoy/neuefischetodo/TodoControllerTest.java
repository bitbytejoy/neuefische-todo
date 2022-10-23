package com.bitbytejoy.neuefischetodo;

import com.bitbytejoy.neuefischetodo.model.Login;
import com.bitbytejoy.neuefischetodo.model.Todo;
import com.bitbytejoy.neuefischetodo.model.TodoStatus;
import com.bitbytejoy.neuefischetodo.model.User;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
public class TodoControllerTest {
    @Autowired
    private MockMvc mvc;

    private static final Gson gson = new Gson();

    @Test
    public void whenCRUDTodo_thenRunCorrectly()
            throws Exception {
        // Register user

        User user = new User();
        user.setName("Tester 01");
        user.setEmail("tester01@example.com");
        user.setPassword("super-secret");

        String userId = (String)gson.fromJson(
                mvc.perform(
                    MockMvcRequestBuilders.post("/users")
                        .content(gson.toJson(user))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString(),
                Map.class
        ).get("id");

        // Login user

        Login login = new Login();
        login.setEmail(user.getEmail());
        login.setPassword(user.getPassword());

        String jwt = (String)gson.fromJson(
                mvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .content(gson.toJson(login))
                                .contentType(MediaType.APPLICATION_JSON)
                ).andReturn().getResponse().getContentAsString(),
                Map.class
        ).get("jwt");

        Map<String, String> loginData = Map.of(
            "jwt", jwt,
            "userId", userId
        );

        // POST /todos

        Todo todo = new Todo();
        todo.setTitle("Test the TODO controller");
        todo.setStatus(TodoStatus.TODO.getTitle());

        mvc.perform(MockMvcRequestBuilders.post("/todos")
            .content(gson.toJson(todo))
            .header(
                HttpHeaders.AUTHORIZATION,
                "Bearer " + loginData.get("jwt")
            ).contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.jsonPath("$.id").isString(),
            MockMvcResultMatchers.jsonPath("$.title").value(todo.getTitle()),
            MockMvcResultMatchers.jsonPath("$.status").value(todo.getStatus()),
            MockMvcResultMatchers.jsonPath("$.userId").value(loginData.get("userId"))
        );

        // GET /todos

        mvc.perform(
            MockMvcRequestBuilders.get("/todos?page=0&size=30")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + loginData.get("jwt")
                )
        ).andExpectAll(
                MockMvcResultMatchers.status().isOk(),
                MockMvcResultMatchers.jsonPath("$.content").isArray(),
                MockMvcResultMatchers.jsonPath("$.content[0].id").isString(),
                MockMvcResultMatchers.jsonPath("$.content[0].title").value(todo.getTitle()),
                MockMvcResultMatchers.jsonPath("$.content[0].status").value(todo.getStatus()),
                MockMvcResultMatchers.jsonPath("$.content[0].userId").value(loginData.get("userId"))
        );

        // GET generated todos ID

        String id = (String)((List<Map<String, Object>>)gson.fromJson(
            mvc.perform(
                MockMvcRequestBuilders.get("/todos?page=0&size=30")
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            "Bearer " + loginData.get("jwt")
                    )
            ).andReturn().getResponse().getContentAsString(),
            Map.class
        ).get("content")).get(0).get("id");

        todo.setId(id);

        // PUT /todos

        todo.setTitle("UPDATED");
        todo.setStatus(TodoStatus.DONE.getTitle());

        mvc.perform(
            MockMvcRequestBuilders.put("/todos/" + todo.getId())
                .content(gson.toJson(todo))
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + loginData.get("jwt")
                ).contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.jsonPath("$.id").isString(),
            MockMvcResultMatchers.jsonPath("$.title").value(todo.getTitle()),
            MockMvcResultMatchers.jsonPath("$.status").value(todo.getStatus()),
            MockMvcResultMatchers.jsonPath("$.userId").value(loginData.get("userId"))
        );

        // GET /todos

        mvc.perform(
            MockMvcRequestBuilders.get("/todos?page=0&size=30")
                .header(
                    HttpHeaders.AUTHORIZATION,
                    "Bearer " + loginData.get("jwt")
                )
        ).andExpectAll(
            MockMvcResultMatchers.status().isOk(),
            MockMvcResultMatchers.jsonPath("$.content").isArray(),
            MockMvcResultMatchers.jsonPath("$.content[0].id").isString(),
            MockMvcResultMatchers.jsonPath("$.content[0].title").value(todo.getTitle()),
            MockMvcResultMatchers.jsonPath("$.content[0].status").value(todo.getStatus()),
            MockMvcResultMatchers.jsonPath("$.content[0].userId").value(loginData.get("userId"))
        );
    }
}
