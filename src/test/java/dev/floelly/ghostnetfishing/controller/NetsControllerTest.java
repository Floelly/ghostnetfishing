package dev.floelly.ghostnetfishing.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NetsControllerTest {
    private static NetsController netController;

    @BeforeAll
    static void beforeAll() {
        netController = new NetsController();
    }

    @Test
    void shouldShowNewNetForm() {
        String controllerReturn = netController.getNewNetForm();
        assertEquals(controllerReturn, "/nets/new");
    }
}