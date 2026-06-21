package fr.uga.gestioncinema;

import fr.uga.gestioncinema.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
class GestionCinemaApplicationTests {

    @Test
    void contextLoads() {
    }

}
