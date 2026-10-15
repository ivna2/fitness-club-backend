package fitnessclub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fitnessclub.model.SessionStatus;
import fitnessclub.model.UserSession;
import fitnessclub.repository.UserSessionRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "app.jwt.secret=test-secret-key-at-least-32-bytes-long!!",
        "server.ssl.enabled=false"
})
@AutoConfigureMockMvc
class SecurityFlowTests {

    // Учётные данные администратора генерируются на каждый запуск и не хранятся в коде
    private static final String ADMIN_EMAIL = "admin-" + UUID.randomUUID() + "@test.local";
    private static final String ADMIN_PASSWORD = "Aa1!" + UUID.randomUUID();
    private static final String STRONG_PASSWORD = "Str0ng!Pass";

    @DynamicPropertySource
    static void adminCredentials(DynamicPropertyRegistry registry) {
        registry.add("admin.email", () -> ADMIN_EMAIL);
        registry.add("admin.password", () -> ADMIN_PASSWORD);
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private UserSessionRepository sessionRepository;

    @Test
    void registrationRejectsWeakPassword() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body("name", "Weak", "email", uniqueEmail(), "password", "12345678")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrationRejectsDuplicateEmail() throws Exception {
        String email = register();
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body("name", "Dup", "email", email, "password", STRONG_PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void endpointsRequireAuthentication() throws Exception {
        mvc.perform(get("/lessons")).andExpect(status().isUnauthorized());
    }

    @Test
    void userCannotCreateTrainerButAdminCan() throws Exception {
        String userToken = login(register(), STRONG_PASSWORD).get("accessToken").asText();
        String adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD).get("accessToken").asText();
        String trainer = body("name", "Test Trainer", "specialization", "Yoga");

        mvc.perform(get("/trainers").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                .andExpect(status().isOk());
        mvc.perform(post("/trainers").header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON).content(trainer))
                .andExpect(status().isForbidden());
        mvc.perform(post("/trainers").header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON).content(trainer))
                .andExpect(status().isCreated());
    }

    @Test
    void basicAuthWriteRequiresCsrfToken() throws Exception {
        String basic = "Basic " + Base64.getEncoder()
                .encodeToString((ADMIN_EMAIL + ":" + ADMIN_PASSWORD).getBytes(StandardCharsets.UTF_8));
        String trainer = body("name", "Basic Trainer", "specialization", "Box");

        mvc.perform(get("/trainers").header(HttpHeaders.AUTHORIZATION, basic))
                .andExpect(status().isOk());
        mvc.perform(post("/trainers").header(HttpHeaders.AUTHORIZATION, basic)
                        .contentType(MediaType.APPLICATION_JSON).content(trainer))
                .andExpect(status().isForbidden());

        MvcResult csrf = mvc.perform(get("/auth/csrf")).andExpect(status().isOk()).andReturn();
        Cookie cookie = csrf.getResponse().getCookie("XSRF-TOKEN");
        String token = json.readTree(csrf.getResponse().getContentAsString()).get("token").asText();
        assertThat(cookie).isNotNull();

        mvc.perform(post("/trainers").header(HttpHeaders.AUTHORIZATION, basic)
                        .cookie(cookie).header("X-XSRF-TOKEN", token)
                        .contentType(MediaType.APPLICATION_JSON).content(trainer))
                .andExpect(status().isCreated());
    }

    @Test
    void refreshTokenCannotBeReused() throws Exception {
        String email = register();
        JsonNode first = login(email, STRONG_PASSWORD);
        String oldRefresh = first.get("refreshToken").asText();

        mvc.perform(get("/lessons").header(HttpHeaders.AUTHORIZATION, "Bearer " + first.get("accessToken").asText()))
                .andExpect(status().isOk());

        JsonNode second = refresh(oldRefresh, 200);
        mvc.perform(get("/lessons").header(HttpHeaders.AUTHORIZATION, "Bearer " + second.get("accessToken").asText()))
                .andExpect(status().isOk());

        // Повторное использование старого refresh-токена -> 401, и все активные сессии отзываются
        refresh(oldRefresh, 401);
        refresh(second.get("refreshToken").asText(), 401);

        List<UserSession> sessions = sessionRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(email)).toList();
        assertThat(sessions).hasSize(2);
        assertThat(sessions).allMatch(s -> s.getStatus() == SessionStatus.REVOKED);
    }

    private String register() throws Exception {
        String email = uniqueEmail();
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content(body("name", "Test User", "email", email, "password", STRONG_PASSWORD)))
                .andExpect(status().isCreated());
        return email;
    }

    private JsonNode login(String email, String password) throws Exception {
        MvcResult result = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", email, "password", password)))
                .andExpect(status().isOk()).andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    private JsonNode refresh(String refreshToken, int expectedStatus) throws Exception {
        MvcResult result = mvc.perform(post("/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(body("refreshToken", refreshToken)))
                .andExpect(status().is(expectedStatus)).andReturn();
        return json.readTree(result.getResponse().getContentAsString());
    }

    private String body(String... keyValues) throws Exception {
        var node = json.createObjectNode();
        for (int i = 0; i < keyValues.length; i += 2) {
            node.put(keyValues[i], keyValues[i + 1]);
        }
        return json.writeValueAsString(node);
    }

    private static String uniqueEmail() {
        return "user-" + UUID.randomUUID() + "@test.local";
    }
}
