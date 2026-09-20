package dev.ahmed.demo.orders.order;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderApiTests {

    private static final String VALID_ORDER = """
            {
              "customerId": "customer-demo-42",
              "items": [
                {"sku": "JAVA-MUG", "quantity": 2, "unitPrice": 12.50}
              ]
            }
            """;

    @Autowired
    MockMvc mvc;

    @Autowired
    OrderRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsAndReadsAnOrder() throws Exception {
        var result = mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.matchesPattern("/api/orders/.+")))
                .andExpect(jsonPath("$.total", is(25.0)))
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        org.assertj.core.api.Assertions.assertThat(location).isNotBlank();
        String id = location.substring(location.lastIndexOf('/') + 1);
        mvc.perform(get("/api/orders/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId", is("customer-demo-42")))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    @Test
    void replaysTheSamePayloadWithoutCreatingADuplicate() throws Exception {
        mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-replay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-replay")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER))
                .andExpect(status().isOk());

        org.assertj.core.api.Assertions.assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void rejectsAnotherPayloadForTheSameIdempotencyKey() throws Exception {
        mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-conflict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER.replace("\"quantity\": 2", "\"quantity\": 3")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));
    }

    @Test
    void rejectsInvalidInput() throws Exception {
        mvc.perform(post("/api/orders")
                        .header("Idempotency-Key", "demo-invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_ORDER.replace("\"quantity\": 2", "\"quantity\": 0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields['items[0].quantity']").exists());
    }

    @Test
    void returnsNotFoundForUnknownOrder() throws Exception {
        mvc.perform(get("/api/orders/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    void exposesHealth() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UP")));
    }
}
