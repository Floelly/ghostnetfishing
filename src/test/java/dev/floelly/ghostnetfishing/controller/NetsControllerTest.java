package dev.floelly.ghostnetfishing.controller;

import dev.floelly.ghostnetfishing.dto.NetDTO;
import dev.floelly.ghostnetfishing.dto.NewNetRequest;
import dev.floelly.ghostnetfishing.model.NetState;
import dev.floelly.ghostnetfishing.service.INewNetService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NetsControllerTest {
    private final static String NEW_NET_CONTENT_TEMPLATE = "/nets/new";
    private static final String NETS_CONTENT_TEMPLATE = "/nets";
    private static final String POST_NEW_NET_REDIRECT_TEMPLATE = NETS_CONTENT_TEMPLATE;

    private static final String NETS_ATTRIBUTE_NAME = "nets";

    @Mock
    INewNetService newNetService;

    @InjectMocks
    private NetsController netController;

    @Test
    void shouldReturnNewNetsContent_onNewNetForm() {
        String controllerResponse = netController.getNewNetFormPage();
        assertEquals(NEW_NET_CONTENT_TEMPLATE, controllerResponse, String.format("The response of the controller should be '%s'", NEW_NET_CONTENT_TEMPLATE));
    }

    @Test
    void shouldCallServiceAndReturnRedirect_onNewNetPost() {
        NewNetRequest newNetRequest = new NewNetRequest("20","20","L");
        doNothing().when(newNetService).addNewNet(eq(newNetRequest));

        String controllerResponse = netController.postNewNet(newNetRequest);

        verify(newNetService).addNewNet(eq(newNetRequest));
        assertEquals("redirect:" + POST_NEW_NET_REDIRECT_TEMPLATE, controllerResponse, String.format("The response of the controller should be a 'redirect:%s'", POST_NEW_NET_REDIRECT_TEMPLATE));
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onNewNetPost(){
        NewNetRequest newNetRequest = new NewNetRequest("20","20","L");

        doThrow(new IllegalArgumentException("invalid net"))
                .when(newNetService).addNewNet(eq(newNetRequest));

        assertThrows(IllegalArgumentException.class, () ->
                netController.postNewNet(newNetRequest));
    }

    @Test
    void shouldCallServiceAndReturnNetsContent_onGetNetsPage() {
        Model model = new ExtendedModelMap();
        NetDTO netDTO1 = new NetDTO(5L, 20.0, 20.0, "L", NetState.RECOVERY_PENDING);
        when(newNetService.getAll())
                .thenReturn(List.of(netDTO1));

        String controllerResponse = netController.getNetsPage(model);

        verify(newNetService).getAll();
        assertEquals(NETS_CONTENT_TEMPLATE, controllerResponse);
        Object netsAttribute = model.getAttribute("nets");
        assertNotNull(netsAttribute);
        assertInstanceOf(List.class, netsAttribute);
        @SuppressWarnings("unchecked")
        List<NetDTO> nets = (List<NetDTO>) netsAttribute;
        assertTrue(nets.stream().allMatch(Objects::nonNull));
        assertEquals(1, nets.size());
        assertThat(nets)
                .extracting(NetDTO::getId)
                .contains(netDTO1.getId());
    }

    @Test
    void shouldThrowException_whenServiceThrowsException_onGetNetsPage(){
        Model model = new ExtendedModelMap();

        doThrow(new RuntimeException()).when(newNetService).getAll();

        assertThrows(RuntimeException.class, () ->
                netController.getNetsPage(model));
    }
}