package app.kumasuke.blog.rof.controller;

import app.kumasuke.blog.rof.Application;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
@ContextConfiguration
@TestInstance(Lifecycle.PER_CLASS)
class HelloControllerTest {
    @Autowired
    private WebApplicationContext applicationContext;
    private MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.applicationContext).build();
    }

    private boolean isSuccessful(MvcResult result) throws IOException {
        String json = result.getResponse().getContentAsString();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.get("message") != null && root.get("exception") == null;
    }

    @Test
    void sayHello() throws Exception {
        final int REPEAT_TIME = 100;

        int successTime = 0;
        for (int i = 0; i < REPEAT_TIME; i++) {
            long startAt = System.currentTimeMillis();
            MvcResult result = mockMvc.perform(get("/say-hello"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            long endAt = System.currentTimeMillis();
            boolean success = isSuccessful(result);
            if (success) successTime += 1;
            System.out.printf("[%03d] success: %b, cost time: %d ms%n",
                              i, success, (endAt - startAt));
        }

        System.out.printf("[summary] total: %d, success: %d%n", REPEAT_TIME, successTime);
    }

    @Test
    void sayHelloFromForeigners() throws Exception {
        final int REPEAT_TIME = 50;

        int successTime = 0;
        for (int i = 0; i < REPEAT_TIME; i++) {
            long startAt = System.currentTimeMillis();
            MvcResult result = mockMvc.perform(get("/say-hello-from-foreigners"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();
            long endAt = System.currentTimeMillis();
            boolean success = isSuccessful(result);
            if (success) successTime += 1;
            System.out.printf("[%03d] success: %b, cost time: %d ms%n",
                              i, success, (endAt - startAt));
        }

        System.out.printf("[summary] total: %d, success: %d%n", REPEAT_TIME, successTime);
    }
}
