package ru.practicum;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = { ShareItServer.class })
@SpringJUnitConfig
class ShareItServerTest {

    @Test
    void contextLoads() {
        assertNotNull(true);
    }

}