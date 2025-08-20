package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.service.INewNetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetsControllerTest {
    private final static String NEW_NET_CONTENT_TEMPLATE = "nets/new";
    private static final String NETS_CONTENT_TEMPLATE = "nets";
    private static final String POST_NEW_NET_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;

    @Mock
    INewNetService newNetService;

    @InjectMocks
    private NetsController netController;

    @Test
    void shouldConnectToNewNetForm() {
        String controllerResponse = netController.getNewNetFormPage();
        assertEquals(NEW_NET_CONTENT_TEMPLATE, controllerResponse, String.format("The response of the controller should be '%s'", NEW_NET_CONTENT_TEMPLATE));
    }

    @Test
    void shouldCallServiceAndReturnRedirectOnSuccess() {
        NewNetRequest newNetRequest = new NewNetRequest(20,20,"L");
        doNothing().when(newNetService).addNewNet(eq(newNetRequest));

        String controllerResponse = netController.postNewNet(newNetRequest);

        verify(newNetService).addNewNet(eq(newNetRequest));
        assertEquals("redirect:" + POST_NEW_NET_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", POST_NEW_NET_REDIRECT_TEMPLATE));
    }
}