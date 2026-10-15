package fitnessclub;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.jwt.secret=test-secret-key-at-least-32-bytes-long!!")
class FitnessclubApplicationTests {

    @Test
    void contextLoads() {
    }

}
